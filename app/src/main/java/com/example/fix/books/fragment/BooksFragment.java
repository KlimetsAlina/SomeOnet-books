package com.example.fix.books.fragment;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.fix.books.BuildConfig;
import com.example.fix.books.R;
import com.example.fix.books.activity.BooksDetails_;
import com.example.fix.books.activity.MyApp;
import com.example.fix.books.adapter.BooksAdapter;
import com.example.fix.books.db.RealmHelper;
import com.example.fix.books.model.Books;
import com.example.fix.books.rest.ApiManager;
import com.example.fix.books.rest.ApiService;
import com.example.fix.books.rest.util.DialogWaitManager;
import com.example.fix.books.ui.RecyclerItemClickListener;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.OptionsMenuItem;
import org.parceler.Parcels;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;


//находим макет меню и макет класса
//@RunWith(AndroidJUnit4.class)
@OptionsMenu(R.menu.main_menu)
@EFragment(R.layout.recyclerview)
public class BooksFragment extends Fragment {
    public static final int TIMEOUT_IN_SECONDS = 3000;
    private static final String TAG = "MainFragment";
    private static final boolean D = BuildConfig.DEBUG;
    private static final long RETRY_COUNT_FOR_REQUEST = 3;
    /* RecyclerView */
    //находим ресайклер вью
    RecyclerView recyclerView;
    //меню
    @OptionsMenuItem
    MenuItem action_search;

    private BooksAdapter booksAdapter;

    //модель
    private Books books;
    /* Realm database variable */
    private Realm mRealm;

    //подписку для rx retrofit он же обработчик
    private CompositeSubscription mSubscriptions = new CompositeSubscription();
    /* SwipeToRefresh */

    //сваип для обновления
    SwipeRefreshLayout mSwipeRefreshContainer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initFragment(view);
        initRecyclerView();
    }




    //метод чтобы с маленькой буквы можно было искать
    public String firstUpperCase(String word) {
        if (word == null || word.isEmpty()) return "";
        return word.substring(0, 1).toUpperCase() + word.substring(1);
    }

    //метод чтобы по нажатию в меню открыть фрагмент
    @OptionsItem(R.id.action_chlg)
    void myMethod() {
        DialogFragment df = new DialogFragmentInfo();
        df.show(getFragmentManager(), "");
    }


//    private void updateArticle(String id, String title, String description) {
//        mRealm.beginTransaction();
//
//        Books books = mRealm.where(Books.class).equalTo("id", id).findFirst();
//        books.setShort_desc(title);
//        books.setName(description);
//        mRealm.commitTransaction();
//
//    }

    private void initFragment(View view) {
        //находим ресайклервью(список книг)
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        //находим адаптер
        booksAdapter = new BooksAdapter(getContext());

        //даем адаптр,чтобы в пустом списке,по умолчанию,загрузился массив книг
        recyclerView.setAdapter(booksAdapter);
        //кидаем свайп и рефрешер
        mSwipeRefreshContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        mSwipeRefreshContainer.setOnRefreshListener(this::retrieveBooks);
    }

    @Override
    public void onStart() {
        super.onStart();
        //говорим реалму мол,братишка,настройки надо заюзать
        mRealm = Realm.getDefaultInstance();
        //        /* First Retrieve questions locally (async) */
        //находим ассинхронно и сортируем по рейтинку ввысь,вроде
        booksAdapter.setList(mRealm.where(Books.class).findAllSortedAsync("rating", Sort.DESCENDING));
     /* Then Retrieve questions from the API */
        retrieveBooks();
    }

    @Override
    public void onResume() {
        super.onResume();
        /* Set Change Listener */
        booksAdapter.getList().addChangeListener(realmChangeListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        /* Remove Change Listener */
        booksAdapter.getList().removeChangeListener(realmChangeListener);
    }

    //наш rx retrofit,считываем с json книженции
    //почему rx retrofit?
    //Отказоустойчивость, отзывчивость, ориентированность на события и масштабируемость —
    // четыре принципа нынче популярного реактивного программирования.
    // Именно следуя им создаётся backend больших систем с одновременной поддержкой десятков тысяч соединений.
    private void retrieveBooks() {
        //если адаптер пуст то
        if (booksAdapter.isEmpty())

            DialogWaitManager.getInstance().showDialog(getContext());
        //находим ретрофит и интерфейс (наш гет )
        ApiService apiService = ApiManager.getApiService();
        //находим обсервл,и кешируем
        //Observable — это как потоки данных (ещё их можно рассматривать как монады), которые могут каким-либо образом получать и отдавать эти самые данные
        Observable<List<Books>> observable = apiService.loadBooks().cache();
        //сабскрайбим
        mSubscriptions.add(
                observable
                        // //сообщить сабскрайберу о том, что есть новые данные
                        .timeout(TIMEOUT_IN_SECONDS, TimeUnit.SECONDS)
                        .retry(RETRY_COUNT_FOR_REQUEST)
                        .doOnNext(bookses -> {
                            try {
                                //cохраняем в бд
                                RealmHelper.copyOrUpdate(bookses);
                            } catch (NullPointerException ex) {
                                //если ничего не пришло
                                if (D) Log.e(TAG, "Null Body response\n" + ex.getMessage());
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread()) //обработка результата - в main thread
                        .subscribeOn(Schedulers.io()) //делаем запрос, преобразование, кэширование в thread pool
                        .subscribe(new Observer<List<Books>>() { //обработчик результата
                            @Override
                            public void onCompleted() {
                                //если все хорошо убираем диалог
                                DialogWaitManager.getInstance().dismissDialog();
                                //и нельзя свайпать
                                mSwipeRefreshContainer.setRefreshing(false);
                            }

                            @Override
                            public void onError(Throwable e) {
                                DialogWaitManager.getInstance().dismissDialog();
                                if (D) Log.e(TAG, "onFailure: " + e.toString());
                                if (isAdded()) { /* Check the user did not quit before updating on UI */
                                    mSwipeRefreshContainer.setRefreshing(false);
                                    Toast.makeText(getContext(), getResources().getString(!MyApp.getInstance().isOnline() ? R.string.error_nointernet : R.string.error_serverfailure), Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onNext(List<Books> bookses) {

                            }
                        }));
    }

    /**
     * Callback for Realm changes
     */
    private RealmChangeListener realmChangeListener = new RealmChangeListener() {
        @Override
        public void onChange() {

         /* Dismiss dialog if it was activated for Online Retrieval but a usable list has been loaded from Realm */
            if (!booksAdapter.isEmpty())
                DialogWaitManager.getInstance().dismissDialog();
            /* Notify Adapter that the data has changed */
            booksAdapter.notifyDataSetChanged();
        }
    };

    @Override
    public void onDestroy() {
        //при уничтожении фрагмента закрываем реалм и отсабскрайбиваеммммся
        super.onDestroy();
        mRealm.close();
        mSubscriptions.unsubscribe();
    }

    private void initRecyclerView() {
        //расположение наших карточек,в данном случае по 1 карточке на экране
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 1);
        //присваиваем расположение к списку книг
        recyclerView.setLayoutManager(gridLayoutManager);
        //слушателя для рв,
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), (view1, position) -> {
            //для передачи данных между объектами
            Bundle bundle = new Bundle();
            //считываем позицию
            books = booksAdapter.getItem(position);
            //заворачиваем модельку
            bundle.putParcelable("example", Parcels.wrap(books));

            //идем в детализацию
            startActivity(new Intent(getContext(), BooksDetails_.class).putExtras(bundle).putExtra("gallery", books.getGallery()));
        }));
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        //находим поиск
        final SearchView view = (SearchView) action_search.getActionView();
        final SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        view.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));

        view.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //ищем по имени
                RealmResults<Books> bookses = mRealm.where(Books.class).contains("name", firstUpperCase(newText)).findAllSorted("rating", Sort.DESCENDING);
                //если нашли то кидаем список
                booksAdapter.setList(bookses);
                booksAdapter.notifyDataSetChanged();
                return true;
            }
        });
    }
}


package com.example.fix.books.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.example.fix.books.R;
import com.example.fix.books.adapter.PracticeAdapter;
import com.example.fix.books.model.Practice;
import com.example.fix.books.rest.ApiService;
import com.example.fix.books.rest.WebService;

import org.androidannotations.annotations.EFragment;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


@EFragment(R.layout.recyclerview)
public class PracticeFragment extends Fragment {

    RecyclerView recyclerView;
    private PracticeAdapter booksAdapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initFragment(view);
    }

    private void initFragment(View view) {
        //находим ресайклервью(список книг)
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        booksAdapter = new PracticeAdapter(getContext());

        //даем адаптр,чтобы в пустом списке,по умолчанию,загрузился массив книг

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 1);
        //присваиваем расположение к списку книг
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(booksAdapter);

        WebService service = WebService.retrofit.create(WebService.class);
        //делаем запрос
        service.getElemnts().enqueue(new Callback<List<Practice>>() {
            //если пришел то отображаем все
            @Override
            public void onResponse(Call<List<Practice>> call, Response<List<Practice>> response) {
                List<Practice> booksList = response.body();
                //находим метод пагинации
                booksAdapter.Pagination(booksList);
            }

            //если нет то нет
            @Override
            public void onFailure(Call<List<Practice>> call, Throwable t) {
            }
        });
        //String.valueof - привести к типу стринг
    }
}


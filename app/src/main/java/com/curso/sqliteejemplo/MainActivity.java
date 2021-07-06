package com.curso.sqliteejemplo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button btnCreate;
    private Button btnDelete;

    private CarsSQLiteHelper carsHelper;
    private SQLiteDatabase db;

    private ListView listView;
    private MyAdapter adapter;

    private List<Car> cars;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView)findViewById(R.id.listView);
        cars = new ArrayList<Car>();

        btnCreate = (Button)findViewById(R.id.buttonCreate);
        btnDelete = (Button)findViewById(R.id.buttonDelete);

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                create();
                update();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeAll();
                update();
            }
        });

        //Abrimos la base de datos 'DBTest1' en modo escritura
        carsHelper = new CarsSQLiteHelper(this, "DBTest1", null, 1);
        db = carsHelper.getWritableDatabase();

        adapter = new MyAdapter(this, cars, R.layout.itemdb);
        listView.setAdapter(adapter);

        update();
    }

    private List<Car> getAllCars(){
        //seleccionamos todos los registros de la tabla cars
        Cursor cursor = db.rawQuery("select * from Cars", null);
        List<Car> list = new ArrayList<Car>();

        if (cursor.moveToFirst()){
            //iteramos sobre el cursor de resultados
            //y vamos rellenando el array que posteriormente devolveremos
            while (cursor.isAfterLast() == false){
                int VIN = cursor.getInt(cursor.getColumnIndex("VIN"));
                String name = cursor.getString(cursor.getColumnIndex("name"));
                String color = cursor.getString(cursor.getColumnIndex("color"));

                list.add(new Car(VIN, name, color));
                cursor.moveToNext();
            }
        }
        return list;
    }

    private void create(){
        //si hemos abierto correctamente la base de datos
        if(db != null){
            //creamos el registro a insertar como objeto ContentValues
            ContentValues nuevoRegistro = new ContentValues();
            //El ID es auto incrementable como declaramos en nuestro CarsSQLiteHelper
            nuevoRegistro.put("name", "Seat");
            nuevoRegistro.put("color", "Black");

            //Insertamos el registro en la base de datos
            db.insert("Cars", null, nuevoRegistro);
        }
    }

    private void removeAll(){

        db.delete("Cars", "", null);
    }

    private void update(){
        //borramos todos los elementos
        cars.clear();
        //cargamos todos los elementos
        cars.addAll(getAllCars());
        //referenciamos el adaptador
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        //cerramos conexion base de datos antes de destruir el activity
        db.close();
        super.onDestroy();
    }
}
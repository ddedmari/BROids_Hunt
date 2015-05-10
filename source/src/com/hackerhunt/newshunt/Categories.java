package com.hackerhunt.newshunt;

import java.io.Serializable;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;

/**
 * Created by ymehta on 10/05/15.
 */
public class Categories extends Activity implements Serializable{

    GridView categoryGrid;
    Button done;
    ArrayList<Boolean> catList = new ArrayList<Boolean>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.categories_layout_new);

        categoryGrid = (GridView)findViewById(R.id.gridCategoriesFirst);
        done = (Button) findViewById(R.id.button_done);

        ArrayList<String> categories = getIntent().getStringArrayListExtra("category");
        final ArrayList<Item> gridArray = new ArrayList<Item>();
        final ArrayList<Item> gridListArray = new ArrayList<Item>();
        ArrayList<Item> catArray = new ArrayList<Item>();
        CustomGridViewAdapter customGridAdapter;
        getActionBar().setTitle("");

        Bitmap art = BitmapFactory.decodeResource(this.getResources(), R.drawable.artnculture);
        Bitmap edu = BitmapFactory.decodeResource(this.getResources(), R.drawable.education_final);
        Bitmap ent = BitmapFactory.decodeResource(this.getResources(), R.drawable.entertainment);
        Bitmap fin = BitmapFactory.decodeResource(this.getResources(), R.drawable.finance_final);
        Bitmap food = BitmapFactory.decodeResource(this.getResources(), R.drawable.food4);
        Bitmap health = BitmapFactory.decodeResource(this.getResources(), R.drawable.health_final);
        Bitmap pol = BitmapFactory.decodeResource(this.getResources(), R.drawable.politics3);
        Bitmap shop = BitmapFactory.decodeResource(this.getResources(), R.drawable.shopping_final);
        Bitmap sport = BitmapFactory.decodeResource(this.getResources(), R.drawable.sports_final);
        Bitmap tech = BitmapFactory.decodeResource(this.getResources(), R.drawable.technology_final);
        Bitmap travel = BitmapFactory.decodeResource(this.getResources(), R.drawable.travel_final);
        Bitmap others = BitmapFactory.decodeResource(this.getResources(), R.drawable.others_final);
        Bitmap art2 = BitmapFactory.decodeResource(this.getResources(), R.drawable.artnculture);
        Bitmap edu2 = BitmapFactory.decodeResource(this.getResources(), R.drawable.education_final_w);
        Bitmap ent2 = BitmapFactory.decodeResource(this.getResources(), R.drawable.entertainment);
        Bitmap fin2 = BitmapFactory.decodeResource(this.getResources(), R.drawable.finance_final_w);
        Bitmap food2 = BitmapFactory.decodeResource(this.getResources(), R.drawable.food4_w);
        Bitmap health2 = BitmapFactory.decodeResource(this.getResources(), R.drawable.health_final_w);
        Bitmap pol2 = BitmapFactory.decodeResource(this.getResources(), R.drawable.politics3_w);
        Bitmap shop2 = BitmapFactory.decodeResource(this.getResources(), R.drawable.shopping_final_w);
        Bitmap sport2 = BitmapFactory.decodeResource(this.getResources(), R.drawable.sports_final_w);
        Bitmap tech2 = BitmapFactory.decodeResource(this.getResources(), R.drawable.technology_final_w);
        Bitmap travel2 = BitmapFactory.decodeResource(this.getResources(), R.drawable.travel_finalw);
        Bitmap others2 = BitmapFactory.decodeResource(this.getResources(), R.drawable.others_final_w);

        gridListArray.add(new Item(ent2, "Entertainment"));
        gridListArray.add(new Item(edu2, "Education"));
        gridListArray.add(new Item(art2, "Art t & Culture"));
        gridListArray.add(new Item(fin2, "Finance"));
        gridListArray.add(new Item(health2, "Health"));
        gridListArray.add(new Item(travel2, "Travel"));
        gridListArray.add(new Item(pol2, "Politics"));
        gridListArray.add(new Item(shop2, "Shopping"));
        gridListArray.add(new Item(sport2, "Sports"));
        gridListArray.add(new Item(tech2, "Technology"));
        gridListArray.add(new Item(others2, "Others"));
        gridListArray.add(new Item(food2, "Food"));

        gridArray.add(new Item(ent, "Entertainment"));
        gridArray.add(new Item(edu, "Education"));
        gridArray.add(new Item(art, "Art t & Culture"));
        gridArray.add(new Item(fin, "Finance"));
        gridArray.add(new Item(health, "Health"));
        gridArray.add(new Item(travel, "Travel"));
        gridArray.add(new Item(pol, "Politics"));
        gridArray.add(new Item(shop, "Shopping"));
        gridArray.add(new Item(sport, "Sports"));
        gridArray.add(new Item(tech, "Technology"));
        gridArray.add(new Item(others, "Others"));
        gridArray.add(new Item(food, "Food"));



        if (categories == null) {
            catArray.add(gridListArray.get(3));
            catArray.add(gridListArray.get(4));
            catArray.add(gridListArray.get(5));
            catArray.add(gridArray.get(6));
            catArray.add(gridArray.get(7));
            catArray.add(gridArray.get(8));
            catArray.add(gridArray.get(9));
            catArray.add(gridArray.get(10));
            catArray.add(gridArray.get(11));
            for (int i = 0; i < 12; i++) {
                if(i<6) {
                    catList.add(true);
                } else {
                    catList.add(false);
                }
            }
        } else {
            for (int i = 0; i < 12; i++) {
                catList.add(false);
            }

            for (int i = 0; i < categories.size(); i++) {
                if (categories.get(i).equalsIgnoreCase("Entertainment")) {
                    catArray.add(gridArray.get(0));
                    catList.set(0, false);
                } else if (categories.get(i).equalsIgnoreCase("Education")) {
                    catArray.add(gridArray.get(1));
                    catList.set(1, false);
                } else if (categories.get(i).equalsIgnoreCase("Art")) {
                    catArray.add(gridArray.get(2));
                    catList.set(2, false);
                } else if (categories.get(i).equalsIgnoreCase("Finance")) {
                    catArray.add(gridArray.get(3));
                    catList.set(3, false);
                } else if (categories.get(i).equalsIgnoreCase("Health")) {
                    catArray.add(gridArray.get(4));
                    catList.set(4, false);
                } else if (categories.get(i).equalsIgnoreCase("Travel")) {
                    catArray.add(gridArray.get(5));
                    catList.set(5, false);
                } else if (categories.get(i).equalsIgnoreCase("Politics")) {
                    catArray.add(gridArray.get(6));
                    catList.set(6, false);
                } else if (categories.get(i).equalsIgnoreCase("Shopping")) {
                    catArray.add(gridArray.get(7));
                    catList.set(7, false);
                } else if (categories.get(i).equalsIgnoreCase("Sports")) {
                    catArray.add(gridArray.get(8));
                    catList.set(8, false);
                } else if (categories.get(i).equalsIgnoreCase("Technology")) {
                    catArray.add(gridArray.get(9));
                    catList.set(9, false);
                } else if (categories.get(i).equalsIgnoreCase("Others")) {
                    catArray.add(gridArray.get(10));
                    catList.set(10, false);
                } else if (categories.get(i).equalsIgnoreCase("Food")) {
                    catArray.add(gridArray.get(11));
                    catList.set(11, false);
                }

            }
        }
        customGridAdapter = new CustomGridViewAdapter(this, R.layout.grid_item_layout, catArray);
        categoryGrid.setAdapter(customGridAdapter);

        categoryGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Item item = (Item) parent.getItemAtPosition(position);
                if (!catList.get(position)) {
                    String category = item.getTitle();
                    for(int i=0;i < gridListArray.size(); i++) {
                        if(category.equals(gridListArray.get(i).getTitle())) {
                            item.setImage(gridListArray.get(i).getImage());
                            ImageView img = (ImageView) view.findViewById(R.id.imgCategory);
                            img.setImageBitmap(item.getImage());
                        }
                    }
                    catList.set(position, true);
                } else {
                    String category = item.getTitle();
                    for(int i=0;i < gridArray.size(); i++) {
                        if(category.equals(gridArray.get(i).getTitle())) {
                            item.setImage(gridArray.get(i).getImage());
                            ImageView img = (ImageView) view.findViewById(R.id.imgCategory);
                            img.setImageBitmap(item.getImage());
                        }
                    }
                    catList.set(position, false);
                }
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cat = new Intent(Categories.this, CategoryActivity.class);
                Bundle bundleObject = new Bundle();
                bundleObject.putSerializable("key", catList);
                cat.putExtras(bundleObject);
                startActivity(cat);
            }
        });
    }
}
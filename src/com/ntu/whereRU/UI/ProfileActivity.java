package com.ntu.whereRU.UI;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

public class ProfileActivity extends ListActivity {
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setListAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, mStrings));
	}

	private String[] mStrings = { "Abbaye de Belloc",
			"Abbaye du Mont des Cats", "Abertam", "Abondance", "Ackawi",
			"Acorn", "Adelost", "Affidelice au Chablis", "Afuega'l Pitu",
			"Airag", "Airedale", "Aisy Cendre", "Allgauer Emmentaler",
			"Alverca", "Ambert", "American Cheese", "Ami du Chambertin",
			"Anejo Enchilado", "Anneau du Vic-Bilh" };
}

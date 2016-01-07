/**
 * @copyright
 * This code is licensed under the Rekisoft Public License.<br/>
 * See http://www.rekisoft.eu/licenses/rkspl.html for more informations.
 */
/**
 * @package eu.rekisoft.android.controls
 * This package contains controls provided by [rekisoft.eu](http://rekisoft.eu/).
 */
package eu.rekisoft.android.demo.editpassword;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Demo activity to show the control.
 *
 * Created on 29.11.2014.
 * @author René Kilczan
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setIcon(R.drawable.ic_launcher);
    }
}
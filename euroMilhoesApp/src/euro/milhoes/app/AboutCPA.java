package euro.milhoes.app;

import euro.milhoes.app.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class AboutCPA extends Activity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String dev_name = "Bruno Ferreira";
        setContentView(R.layout.about_cpa);
        //obter Extra com o nome do programador/entidade
        Intent thisIntent = getIntent();
        if (thisIntent.hasExtra("extra_dev_name"))
        {
            dev_name = thisIntent.getStringExtra("extra_dev_name");
        }
        TextView tv1 = (TextView) findViewById(R.id.cpa_disclamer);
        tv1.setText(getString(R.string.cpa_about)+" "+dev_name+" "+getString(R.string.cpa_about2));
    }
}

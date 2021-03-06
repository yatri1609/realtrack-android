package com.hackforchange.views.welcome;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.hackforchange.R;
import com.hackforchange.backend.activities.ParticipationDAO;
import com.hackforchange.common.StyledButton;
import com.hackforchange.models.activities.Participation;
import com.hackforchange.views.help.HelpDialog;
import com.hackforchange.views.participationsdonesummaries.ParticipationSummaryActivity;
import com.hackforchange.views.participationspending.PendingParticipationActivity;
import com.hackforchange.views.projectsactivities.AllProjectsActivitiesActivity;

/**
 * This is the home screen of the app.
 * @author Raj
 */
public class WelcomeActivity extends SherlockFragmentActivity implements OnClickListener{
  private ArrayList<String> homeitems_data;
  private ArrayList<Participation> unservicedParticipation_data;
  private LinearLayout welcomeActivityLinearLayout;
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_welcomeactivity);
  }

  @Override
  public void onResume() {
    super.onResume();
    
    welcomeActivityLinearLayout = (LinearLayout) findViewById(R.id.welcomeactivitylinearlayout);
    welcomeActivityLinearLayout.removeAllViews();

    homeitems_data = new ArrayList<String>();
    homeitems_data.add(getResources().getString(R.string.fa_list)+" My Projects");
    homeitems_data.add(getResources().getString(R.string.fa_table)+" My Data");

    ParticipationDAO pDao = new ParticipationDAO(getApplicationContext());
    unservicedParticipation_data = pDao.getAllUnservicedParticipations();
    if (unservicedParticipation_data.size() != 0) {
      homeitems_data.add(getResources().getString(R.string.fa_calendar)+" Pending (" + unservicedParticipation_data.size() + ")");
    }

    // populate the home items list
    updateHomeItemsList();
  }

  // create actionbar menu
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getSupportMenuInflater();
    inflater.inflate(R.menu.welcomeactivitymenu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_help:
        HelpDialog helpDialog = new HelpDialog();
        helpDialog.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        helpDialog.show(getSupportFragmentManager(), "helpdialog");
        break;
      default:
        return super.onOptionsItemSelected(item);
    }

    return true;
  }

  void updateHomeItemsList() {
    LayoutInflater inflater = getLayoutInflater();

    for(int i=0;i<homeitems_data.size();++i){
      StyledButton homeItemBtn = (StyledButton) inflater.inflate(R.layout.row_homeitems, welcomeActivityLinearLayout, false);
      homeItemBtn.setId(i);
      homeItemBtn.setOnClickListener(this);
      homeItemBtn.setText(homeitems_data.get(i));
      welcomeActivityLinearLayout.addView(homeItemBtn);
    }
  }

  @Override
  public void onClick(View v) {
    int pos = v.getId();
    switch (pos) {
      case 0: // MY PROJECTS
        Intent newActivity = new Intent(this, AllProjectsActivitiesActivity.class);
        this.startActivity(newActivity);
        overridePendingTransition(R.anim.animation_slideinright, R.anim.animation_slideoutleft);
        break;
      case 1: // MY DATA
        newActivity = new Intent(this, ParticipationSummaryActivity.class);
        this.startActivity(newActivity);
        overridePendingTransition(R.anim.animation_slideinright, R.anim.animation_slideoutleft);
        break;
      case 2: // PENDING
        newActivity = new Intent(this, PendingParticipationActivity.class);
        this.startActivity(newActivity);
        overridePendingTransition(R.anim.animation_slideinright, R.anim.animation_slideoutleft);
        break;
    }
  }
}
package com.automatizacion.alcohomidete.main;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.fragment.app.Fragment;
import com.automatizacion.alcohomidete.R;
import com.automatizacion.alcohomidete.dbconnections.DBCConnectionHelper;
import com.automatizacion.alcohomidete.people.User;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ScoreFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScoreFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    ImageButton relodeButton=null;
    TableLayout scoreTable=null;
    TableRow scoreRow=null;
    private User actualUser=null;

    public ScoreFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ScoreFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ScoreFragment newInstance(String param1, String param2) {
        ScoreFragment fragment = new ScoreFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_score, container, false);
        scoreTable=v.findViewById(R.id.sfScoreTable);
        try {
            relodeButton=v.findViewById(R.id.sfRelodeButton);

            relodeButton.setOnClickListener(loadGeneralScores);
        }catch (Exception e){e.printStackTrace();}
        return v;
    }

    View.OnClickListener loadGeneralScores=new View.OnClickListener() {
        @SuppressLint({"Range", "ResourceAsColor"})
        @Override
        public void onClick(View v) {
            try {
                DBCConnectionHelper conn=new DBCConnectionHelper(getContext());
                SQLiteDatabase db=conn.getWritableDatabase();
                Cursor scoreData=db.rawQuery("SELECT ALCOHOLIC_GRADE, REGISTER_DATE, REGISTER_TIME " +
                        "FROM Score WHERE RECORD_ID='"+actualUser.getScoreID()+"' ORDER BY 2,3 DESC;",null);
                scoreData.moveToFirst();
                do {
                    scoreRow=new TableRow(getContext());
                    TextView dataLV=new TextView(getContext());
                    TextView dataD=new TextView(getContext());
                    TextView dataH=new TextView(getContext());
                    dataLV.setBackgroundColor(R.color.white);
                    dataLV.setTextColor(R.color.black);
                    dataLV.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    dataLV.setTextSize(20);
                    dataLV.setText(scoreData.getString(scoreData.getColumnIndex("ALCOHOLIC_GRADE")));
                    scoreRow.addView(dataLV);
                    dataD.setBackgroundColor(R.color.white);
                    dataD.setTextColor(R.color.black);
                    dataD.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    dataD.setTextSize(20);
                    dataD.setText(scoreData.getString(scoreData.getColumnIndex("REGISTER_DATE")));
                    scoreRow.addView(dataD);
                    dataH.setBackgroundColor(R.color.white);
                    dataH.setTextColor(R.color.black);
                    dataH.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    dataH.setTextSize(20);
                    dataH.setText(scoreData.getString(scoreData.getColumnIndex("REGISTER_TIME")));
                    scoreRow.addView(dataH);
                    scoreTable.addView(scoreRow);
                } while (scoreData.moveToNext());
            }catch (Exception e){e.printStackTrace();}
        }
    };

    public void setScore(User user){
        this.actualUser=user;
    }
}
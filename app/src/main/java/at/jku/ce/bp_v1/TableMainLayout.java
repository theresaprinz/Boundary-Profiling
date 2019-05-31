package at.jku.ce.bp_v1;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import at.jku.ce.bp_v1.classes.RW;
import at.jku.ce.bp_v1.fragments.ProfilingEDKFragment;
import at.jku.ce.bp_v1.fragments.ProfilingKKFragment;
import at.jku.ce.bp_v1.classes.Durchdringung;
import at.jku.ce.bp_v1.classes.Files;
import at.jku.ce.bp_v1.classes.Kontext;
import at.jku.ce.bp_v1.classes.Profile;
import at.jku.ce.bp_v1.classes.Rolle;

import static at.jku.ce.bp_v1.R.drawable;
import static at.jku.ce.bp_v1.classes.Files.PROFILE;
import static java.lang.String.valueOf;


// Quelle: https://www.androidcode.ninja/android-scroll-table-fixed-header-column/

public class TableMainLayout extends RelativeLayout {
    private Context context;
    private FragmentActivity activity;

    private List<String> headers;
    private List<Integer> headerCellsWidth;
    private TableLayout tableA_unscrollableHeader;
    private TableLayout tableB_scrollableHeaderRow;
    private TableLayout tableC_scrollableHeaderColumn;
    private TableLayout tableD_scrollableBody;
    private HorizontalScrollView horizontalScrollViewB;
    private HorizontalScrollView horizontalScrollViewD;
    private ScrollView scrollViewC;
    private ScrollView scrollViewD;


    private RW<Profile, Durchdringung> profileRW;
    private RW<Kontext, Rolle> kontextRW;
    private Map<Kontext, List<Rolle>> kontextList;
    private Map<Profile, List<Durchdringung>> profileList;

    public TableMainLayout(Context context, FragmentActivity activity) {

        super(context);

        this.context = context;
        this.activity = activity;

        initData();

        // Hauptkomponenten initialisieren (TableLayouts, HorizontalScrollView, ScrollView)
        this.initComponents();
        this.setComponentsId();
        this.setScrollViewAndHorizontalScrollViewTag();

        this.horizontalScrollViewB.addView(this.tableB_scrollableHeaderRow);
        this.scrollViewC.addView(this.tableC_scrollableHeaderColumn);
        this.scrollViewD.addView(this.horizontalScrollViewD);
        this.horizontalScrollViewD.addView(this.tableD_scrollableBody);

        this.addComponentToMainLayout();

        this.addTableRowToTableA();
        this.addTableRowToTableB();

        this.resizeHeaderHeight();
        this.getTableRowHeaderCellWidth();

        this.generateTableC_AndTable_B();

        this.resizeBodyTableRowHeight();
    }

    private void initData() {
        profileRW = new RW<>();
        profileList = profileRW.readMap(context, valueOf(PROFILE));

        kontextRW = new RW<>();
        kontextList = kontextRW.readMap(context, String.valueOf(Files.KONTEXTE));

        headers = new ArrayList<>();

        headers.add(context.getString(R.string.home_tableA));

        for (Kontext k : kontextList.keySet()) {
            headers.add(k.toString());
        }

        headerCellsWidth = new ArrayList<>();
    }

    private void initComponents() {

        this.tableA_unscrollableHeader = new TableLayout(this.context);
        this.tableB_scrollableHeaderRow = new TableLayout(this.context);
        this.tableC_scrollableHeaderColumn = new TableLayout(this.context);
        this.tableD_scrollableBody = new TableLayout(this.context);

        this.horizontalScrollViewB = new MyHorizontalScrollView(this.context);
        this.horizontalScrollViewD = new MyHorizontalScrollView(this.context);

        this.scrollViewC = new MyScrollView(this.context);
        this.scrollViewD = new MyScrollView(this.context);

        this.horizontalScrollViewB.setBackgroundColor(getResources().getColor(R.color.background));

    }

    private void setComponentsId() {
        this.tableA_unscrollableHeader.setId(R.id.idA);
        this.horizontalScrollViewB.setId(R.id.idB);
        this.scrollViewC.setId(R.id.idC);
        this.scrollViewD.setId(R.id.idD);
    }

    private void setScrollViewAndHorizontalScrollViewTag() {

        this.horizontalScrollViewB.setTag("horizontal scroll view b");
        this.horizontalScrollViewD.setTag("horizontal scroll view d");

        this.scrollViewC.setTag("scroll view c");
        this.scrollViewD.setTag("scroll view d");
    }

    private void addComponentToMainLayout() {
        RelativeLayout.LayoutParams componentB_Params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        componentB_Params.addRule(RelativeLayout.RIGHT_OF, this.tableA_unscrollableHeader.getId());

        RelativeLayout.LayoutParams componentC_Params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        componentC_Params.addRule(RelativeLayout.BELOW, this.tableA_unscrollableHeader.getId());

        RelativeLayout.LayoutParams componentD_Params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        componentD_Params.addRule(RelativeLayout.RIGHT_OF, this.scrollViewC.getId());
        componentD_Params.addRule(RelativeLayout.BELOW, this.horizontalScrollViewB.getId());

        this.addView(this.tableA_unscrollableHeader);
        this.addView(this.horizontalScrollViewB, componentB_Params);
        this.addView(this.scrollViewC, componentC_Params);
        this.addView(this.scrollViewD, componentD_Params);
    }

    private void addTableRowToTableA() {
        this.tableA_unscrollableHeader.addView(this.componentATableRow());
    }

    private void addTableRowToTableB() {
        this.tableB_scrollableHeaderRow.addView(this.componentBTableRow());
    }

    TableRow componentATableRow() {
        TableRow componentATableRow = new TableRow(this.context);
        TextView textView = this.headerTextView(this.headers.get(0));
        componentATableRow.addView(textView);

        return componentATableRow;
    }

    TableRow componentBTableRow() {
        TableRow componentBTableRow = new TableRow(this.context);
        int headerFieldCount = this.headers.size();

        TableRow.LayoutParams params = new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        params.setMargins(2, 0, 0, 0);

        for (int x = 0; x < (headerFieldCount - 1); x++) {
            TextView textView = this.headerTextView(this.headers.get(x + 1));
            textView.setLayoutParams(params);
            componentBTableRow.addView(textView);
        }

        return componentBTableRow;
    }

    private void generateTableC_AndTable_B() {
        for (Kontext k : kontextList.keySet()) {
            for (Rolle r : kontextList.get(k)) {
                TableRow tableRowForTableC = this.tableRowForTableC(k + "\n" + r);
                TableRow taleRowForTableD = this.taleRowForTableD(k, r);

                tableRowForTableC.setBackgroundColor(getResources().getColor(R.color.background));
                taleRowForTableD.setBackgroundColor(getResources().getColor(R.color.background));

                this.tableC_scrollableHeaderColumn.addView(tableRowForTableC);
                this.tableD_scrollableBody.addView(taleRowForTableD);
            }
        }
    }

    TableRow tableRowForTableC(String text) {
        TableRow.LayoutParams params = new TableRow.LayoutParams(this.headerCellsWidth.get(0)+15, LayoutParams.MATCH_PARENT);
        params.setMargins(0, 2, 0, 0);

        TableRow tableRowForTableC = new TableRow(this.context);
        TextView textView = this.bodyTextView(text);
        tableRowForTableC.addView(textView, params);

        return tableRowForTableC;
    }

    TableRow taleRowForTableD(Kontext qKontext, Rolle qRolle) {
        TableRow taleRowForTableD = new TableRow(this.context);

        for (int x = 0; x < kontextList.keySet().size(); x++) {
            TableRow.LayoutParams params = new TableRow.LayoutParams(headerCellsWidth.get(x + 1), LayoutParams.MATCH_PARENT);
            params.setMargins(2, 2, 2, 2);

            LinearLayout durchdringungen = new LinearLayout(context);
            durchdringungen.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (((LinearLayout) view).getChildAt(0) != null &&
                            !( ((TextView) ((LinearLayout) ((LinearLayout) view).getChildAt(0)).getChildAt(0)).getText().toString()).equals("")) {
                        ((BottomNavigationView) activity.findViewById(R.id.bottom_navigation)).setSelectedItemId(R.id.nav_profiling);

                        ProfilingEDKFragment edkFragment = new ProfilingEDKFragment();
                        Bundle arguments = new Bundle();
                        arguments.putString("ProfilID", ((TextView) ((LinearLayout) ((LinearLayout) view).getChildAt(0)).getChildAt(0)).getText().toString());
                        edkFragment.setArguments(arguments);

                        FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.profiling_fragment, edkFragment, "EDKFragment");
                        fragmentTransaction.add(R.id.profiling_fragment_edk_kk, new ProfilingKKFragment(), "EDKKKFragment");
                        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                        fragmentTransaction.addToBackStack("HomeToEDK");
                        fragmentTransaction.commit();

                    } else {
                        Toast.makeText(context, R.string.toast_kein_profiling, Toast.LENGTH_SHORT).show();
                    }

                }
            });

            for (Profile p : profileList.keySet()) {
                if (p.getQuellkontext().equalName(qKontext) && p.getQuellrolle().equalName(qRolle) && p.getZielkontext().equalName((Kontext) kontextList.keySet().toArray()[x])) {
                    for (Durchdringung d : profileList.get(p)) {
                        LinearLayout dView = new LinearLayout(context);
                        dView.setVerticalGravity(Gravity.CENTER);
                        dView.setPaddingRelative(15, 15, 15, 15);
                        dView.setLayoutParams(new TableRow.LayoutParams(headerCellsWidth.get(x + 1) / profileList.get(p).size(), LayoutParams.MATCH_PARENT));

                        TextView tv = new TextView(context);
                        tv.setText(p.get_id().toString());
                        tv.setLayoutParams(new TableRow.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
                        tv.setGravity(Gravity.CENTER);
                        tv.setTextColor(ContextCompat.getColorStateList(context, R.color.transparent));
                        tv.setTextSize(1);
                        dView.addView(tv);

                        switch (d.getEindringlichkeit()) {
                            case HOCH:
                                dView.setBackgroundResource(R.color.hoch);
                                break;
                            case MITTEL:
                                dView.setBackgroundResource(R.color.mittel);
                                break;
                            case GERING:
                                dView.setBackgroundResource(R.color.gering);
                                break;
                            case UNDEFINIERT:
                                dView.setBackgroundResource(R.color.undefiniert);
                                break;
                            case UNERLAUBT:
                                tv.setText("");
                                dView.setBackgroundResource(R.color.unerlaubt);
                                break;
                            case AUSSTEHEND:
                                dView.setBackgroundResource(R.color.ausstehen);
                                break;
                            default:
                                dView.setBackgroundResource(R.color.ausstehen);
                                break;
                        }



                        switch (d.getAkzeptanz()) {
                            case AKZEPTANZ:
                                tv.setBackgroundResource(drawable.ic_check_black_24dp);
                                break;
                            case ABLEHNUNG:
                                tv.setBackgroundResource(drawable.ic_close_black_24dp);
                                break;
                            case SITUATIONSABHAENGIG:
                                tv.setBackgroundResource(R.drawable.ic_situational);
                                break;
                            case UNDEFINIERT:
                                tv.setBackgroundResource(R.drawable.ic_undefined);
                                tv.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.black)));
                                break;
                            case AUSSTEHEND:
                                tv.setBackgroundResource(R.drawable.ic_undefined);
                                tv.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.greyDark)));
                                break;
                            default:
                                tv.setBackgroundResource(R.drawable.ic_undefined);
                                tv.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.greyDark)));
                                break;
                        }
                        durchdringungen.addView(dView);
                    }
                }
            }

            taleRowForTableD.addView(durchdringungen, params);
        }

        return taleRowForTableD;

    }

    TextView bodyTextView(String label) {
        TextView bodyTextView = new TextView(this.context);
        bodyTextView.setBackgroundColor(getResources().getColor(R.color.white));
        bodyTextView.setText(label);
        bodyTextView.setTextSize(16);
        bodyTextView.setGravity(Gravity.CENTER);
        bodyTextView.setPadding(20, 30, 20, 30);

        return bodyTextView;
    }

    TextView headerTextView(String label) {
        TextView headerTextView = new TextView(this.context);
        headerTextView.setBackgroundColor(getResources().getColor(R.color.white));
        headerTextView.setText(label);
        headerTextView.setTextSize(16);
        headerTextView.setGravity(Gravity.TOP);
        headerTextView.setPadding(20, 10, 20, 10);

        return headerTextView;
    }

    void resizeHeaderHeight() {
        TableRow productNameHeaderTableRow = (TableRow) this.tableA_unscrollableHeader.getChildAt(0);
        TableRow productInfoTableRow = (TableRow) this.tableB_scrollableHeaderRow.getChildAt(0);

        int rowAHeight = this.viewHeight(productNameHeaderTableRow);
        int rowBHeight = this.viewHeight(productInfoTableRow);

        TableRow tableRow = rowAHeight < rowBHeight ? productNameHeaderTableRow : productInfoTableRow;
        int finalHeight = rowAHeight > rowBHeight ? rowAHeight : rowBHeight;

        this.matchLayoutHeight(tableRow, finalHeight);
    }

    void getTableRowHeaderCellWidth() {
        int tableAChildCount = ((TableRow) this.tableA_unscrollableHeader.getChildAt(0)).getChildCount();
        int tableBChildCount = ((TableRow) this.tableB_scrollableHeaderRow.getChildAt(0)).getChildCount();
        ;

        for (int x = 0; x < (tableAChildCount + tableBChildCount); x++) {

            if (x == 0) {
                this.headerCellsWidth.add(x, this.viewWidth(((TableRow) this.tableA_unscrollableHeader.getChildAt(0)).getChildAt(x)));
            } else {
                this.headerCellsWidth.add(x, this.viewWidth(((TableRow) this.tableB_scrollableHeaderRow.getChildAt(0)).getChildAt(x - 1)));
            }

        }
    }

    void resizeBodyTableRowHeight() {
        int tableC_ChildCount = this.tableC_scrollableHeaderColumn.getChildCount();

        for (int x = 0; x < tableC_ChildCount; x++) {

            TableRow productNameHeaderTableRow = (TableRow) this.tableC_scrollableHeaderColumn.getChildAt(x);
            TableRow productInfoTableRow = (TableRow) this.tableD_scrollableBody.getChildAt(x);

            int rowAHeight = this.viewHeight(productNameHeaderTableRow);
            int rowBHeight = this.viewHeight(productInfoTableRow);

            TableRow tableRow = rowAHeight < rowBHeight ? productNameHeaderTableRow : productInfoTableRow;
            int finalHeight = rowAHeight > rowBHeight ? rowAHeight : rowBHeight;

            this.matchLayoutHeight(tableRow, finalHeight);
        }

    }

    private void matchLayoutHeight(TableRow tableRow, int height) {
        int tableRowChildCount = tableRow.getChildCount();

        if (tableRow.getChildCount() == 1) {

            View view = tableRow.getChildAt(0);
            TableRow.LayoutParams params = (TableRow.LayoutParams) view.getLayoutParams();
            params.height = height - (params.bottomMargin + params.topMargin);

            return;
        }

        for (int x = 0; x < tableRowChildCount; x++) {

            View view = tableRow.getChildAt(x);

            TableRow.LayoutParams params = (TableRow.LayoutParams) view.getLayoutParams();

            if (!isTheHeighestLayout(tableRow, x)) {
                params.height = height - (params.bottomMargin + params.topMargin);
                return;
            }
        }

    }

    private boolean isTheHeighestLayout(TableRow tableRow, int layoutPosition) {
        int tableRowChildCount = tableRow.getChildCount();
        int heighestViewPosition = -1;
        int viewHeight = 0;

        for (int x = 0; x < tableRowChildCount; x++) {
            View view = tableRow.getChildAt(x);
            int height = this.viewHeight(view);

            if (viewHeight < height) {
                heighestViewPosition = x;
                viewHeight = height;
            }
        }

        return heighestViewPosition == layoutPosition;
    }

    private int viewHeight(View view) {
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        return view.getMeasuredHeight();
    }

    private int viewWidth(View view) {
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        return view.getMeasuredWidth();
    }

    class MyHorizontalScrollView extends HorizontalScrollView {
        public MyHorizontalScrollView(Context context) {
            super(context);
        }

        @Override
        protected void onScrollChanged(int l, int t, int oldl, int oldt) {
            String tag = (String) this.getTag();

            if (tag.equalsIgnoreCase("horizontal scroll view b")) {
                horizontalScrollViewD.scrollTo(l, 0);
            } else {
                horizontalScrollViewB.scrollTo(l, 0);
            }
        }

    }

    class MyScrollView extends ScrollView {
        public MyScrollView(Context context) {
            super(context);
        }

        @Override
        protected void onScrollChanged(int l, int t, int oldl, int oldt) {

            String tag = (String) this.getTag();

            if (tag.equalsIgnoreCase("scroll view c")) {
                scrollViewD.scrollTo(0, t);
            } else {
                scrollViewC.scrollTo(0, t);
            }
        }
    }
}
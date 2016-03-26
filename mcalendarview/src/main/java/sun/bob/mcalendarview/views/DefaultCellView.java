package sun.bob.mcalendarview.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.TextView;

import sun.bob.mcalendarview.CellConfig;
import sun.bob.mcalendarview.R;

/**
 * Created by bob.sun on 15/8/28.
 */
public class DefaultCellView extends BaseCellView {
    public TextView textView;
//    public ImageView point;
//    public TextView lunarTextView;
    private AbsListView.LayoutParams matchParentParams;
    public DefaultCellView(Context context) {
        super(context);
        this.mContext = context;
        initLayout();
    }
private Context mContext;
    public DefaultCellView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initLayout();
    }

    private void initLayout(){
        matchParentParams = new AbsListView.LayoutParams((int) CellConfig.cellWidth, (int) CellConfig.cellHeight);
        this.setLayoutParams(matchParentParams);
        this.setOrientation(VERTICAL);
        this.setGravity(Gravity.CENTER);
//        textView = new TextView(getContext());
//        textView.setGravity(Gravity.CENTER);
//        textView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, (float) 1.0));
//        ImageView imageView = new ImageView(getContext());
//        imageView.setLayoutParams(new LayoutParams(5,5,(float) 1.0));
//        imageView.setBackgroundResource(R.drawable.point);
        View view = View.inflate(getContext(),R.layout.calendar_date,null);
        textView = (TextView) view.findViewById(R.id.solar);
//        lunarTextView = (TextView) view.findViewById(R.id.lunar);
//         point = (ImageView) view.findViewById(R.id.has_order);

        this.addView(view);
//        this.addView(imageView);
    }

    @Override
    public void setDisplayText(String text) {
        textView.setText(text);
    }

    @Override
    public void setDisplayLunarText(String text) {

//        lunarTextView.setText(text);
    }

    @Override
    public void showPoint(boolean b) {
//        if(b){
//            point.setVisibility(View.VISIBLE);
//        }else{
//            point.setVisibility(View.INVISIBLE);
//        }
    }

    @Override
    protected void onMeasure(int measureWidthSpec,int measureHeightSpec){
        super.onMeasure(measureWidthSpec, measureHeightSpec);
    }
}

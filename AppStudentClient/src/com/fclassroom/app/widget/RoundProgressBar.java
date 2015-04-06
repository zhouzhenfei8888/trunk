package com.fclassroom.app.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.fclassroom.appstudentclient.R;

/**
 * 仿iphone带进度的进度条，线程安全的View，可直接在线程中更新进度
 *
 * @author xiaanming
 */
public class RoundProgressBar extends View {
    /**
     * 画笔对象的引用
     */
    private Paint paint;

    /**
     * 圆环的颜色
     */
    private int roundColor;

    private int roundColor2;
    private int roundColor3;
    private int roundColor4;
    /**
     * 圆环进度的颜色
     */
    private int roundProgressColor;
    private int roundProgressColor2;
    private int roundProgressColor3;
    private int roundProgressColor4;
    /**
     * 中间进度百分比的字符串的颜色
     */
    private int textColor;

    /**
     * 中间进度百分比的字符串的字体
     */
    private float textSize;

    /**
     * 中间文字内容
     */
    private String text;

    /**
     * 圆环的宽度
     */
    private float roundWidth;
    private float roundWidth2;
    private float roundWidth3;
    private float roundWidth4;

    /**
     * 最大进度
     */
    private int max;

    /**
     * 当前进度
     */
    private int progress;
    private int progress2;
    /**
     * 是否显示中间的进度
     */
    private boolean textIsDisplayable;

    /**
     * 进度的风格，实心或者空心
     */
    private int style;

    public static final int STROKE = 0;
    public static final int FILL = 1;
    private Context context;

    public RoundProgressBar(Context context) {
        this(context, null);
    }

    public RoundProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        paint = new Paint();


        TypedArray mTypedArray = context.obtainStyledAttributes(attrs,
                R.styleable.RoundProgressBar);

        //获取自定义属性和默认值
        roundColor = mTypedArray.getColor(R.styleable.RoundProgressBar_roundColor, Color.RED);
        roundColor2 = mTypedArray.getColor(R.styleable.RoundProgressBar_roundColor2, Color.RED);
        roundColor3 = mTypedArray.getColor(R.styleable.RoundProgressBar_roundColor3, Color.RED);
        roundColor4 = mTypedArray.getColor(R.styleable.RoundProgressBar_roundColor4, Color.RED);
        roundProgressColor = mTypedArray.getColor(R.styleable.RoundProgressBar_roundProgressColor, Color.GREEN);
        roundProgressColor2 = mTypedArray.getColor(R.styleable.RoundProgressBar_roundProgressColor2, Color.GREEN);
        roundProgressColor3 = mTypedArray.getColor(R.styleable.RoundProgressBar_roundProgressColor3, Color.GREEN);
        roundProgressColor4 = mTypedArray.getColor(R.styleable.RoundProgressBar_roundProgressColor4, Color.GREEN);
        textColor = mTypedArray.getColor(R.styleable.RoundProgressBar_textColor, Color.GREEN);
        textSize = mTypedArray.getDimension(R.styleable.RoundProgressBar_textSize, 15);
        roundWidth = mTypedArray.getDimension(R.styleable.RoundProgressBar_roundWidth, 5);
        roundWidth2 = mTypedArray.getDimension(R.styleable.RoundProgressBar_roundWidth2, 5);
        roundWidth3 = mTypedArray.getDimension(R.styleable.RoundProgressBar_roundWidth3, 5);
        roundWidth4 = mTypedArray.getDimension(R.styleable.RoundProgressBar_roundWidth4, 5);
        max = mTypedArray.getInteger(R.styleable.RoundProgressBar_max, 100);
        textIsDisplayable = mTypedArray.getBoolean(R.styleable.RoundProgressBar_textIsDisplayable, true);
        style = mTypedArray.getInt(R.styleable.RoundProgressBar_style, 0);

        mTypedArray.recycle();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        /**
         * 画最外层的大圆环
         */
        int centre = getWidth() / 2; //获取圆心的x坐标
        int radius = (int) (centre - roundWidth / 2); //圆环的半径
        paint.setColor(roundColor); //设置圆环的颜色
        paint.setStyle(Paint.Style.STROKE); //设置空心
        paint.setStrokeWidth(roundWidth); //设置圆环的宽度
        paint.setAntiAlias(true);  //消除锯齿
        canvas.drawCircle(centre, centre, radius, paint); //画出圆环

        Log.e("log", centre + "");

        //圆圈2
        int radius2 = (int) (centre - roundWidth - roundWidth2 / 2); //圆环的半径
        paint.setColor(roundColor2); //设置圆环的颜色
        paint.setStyle(Paint.Style.STROKE); //设置空心
        paint.setStrokeWidth(roundWidth2); //设置圆环的宽度
        paint.setAntiAlias(true);  //消除锯齿
        canvas.drawCircle(centre, centre, radius2, paint); //画出圆环

        //圆圈3
        int radius3 = (int) (centre - roundWidth - roundWidth2 -roundWidth3/2); //圆环的半径
        paint.setColor(roundColor3); //设置圆环的颜色
        paint.setStyle(Paint.Style.STROKE); //设置空心
        paint.setStrokeWidth(roundWidth3); //设置圆环的宽度
        paint.setAntiAlias(true);  //消除锯齿
        canvas.drawCircle(centre, centre, radius3, paint); //画出圆环

        //圆圈4
        int radius4 = (int) (centre - roundWidth - roundWidth2 -roundWidth3-roundWidth4/2); //圆环的半径
        paint.setColor(roundColor4); //设置圆环的颜色
        paint.setStyle(Paint.Style.STROKE); //设置空心
        paint.setStrokeWidth(roundWidth4); //设置圆环的宽度
        paint.setAntiAlias(true);  //消除锯齿
        canvas.drawCircle(centre, centre, radius4, paint); //画出圆环

        /**
         * 画进度百分比
         */
        paint.setStrokeWidth(0);
        paint.setColor(textColor);
        paint.setTextSize(textSize);
        paint.setTypeface(Typeface.DEFAULT_BOLD); //设置字体
        int percent = (int) (((float) progress / (float) max) * 100);  //中间的进度百分比，先转换成float在进行除法运算，不然都为0
        float textWidth = paint.measureText(percent + "%");   //测量字体宽度，我们需要根据字体的宽度设置在圆环中间
        if (textIsDisplayable && percent != 0 && style == STROKE) {
            canvas.drawText(text, centre - textWidth / 2, centre + textSize / 2, paint); //画出进度百分比
        }
        //右边加分钟
        paint.setStrokeWidth(0);
        paint.setColor(Color.parseColor("#ff22ef8c"));
        paint.setTextSize(textSize / 2);
        paint.setTypeface(Typeface.DEFAULT); //设置字体
        float textWidthMin = paint.measureText("分钟");   //测量字体宽度，我们需要根据字体的宽度设置在圆环中间

        if (textIsDisplayable && percent != 0 && style == STROKE) {
            canvas.drawText("分钟", centre + textWidth / 2 + textSize / 4, centre + textSize / 2, paint); //画出进度百分比
        }
        //下面加字符串
        paint.setStrokeWidth(0);
        paint.setColor(textColor);
        paint.setTextSize(textSize / 2);
        paint.setTypeface(Typeface.DEFAULT); //设置字体
        float textWidthDown = paint.measureText("已节约时间");   //测量字体宽度，我们需要根据字体的宽度设置在圆环中间

        if (textIsDisplayable && percent != 0 && style == STROKE) {
            canvas.drawText("已节约时间", centre - textWidthDown / 2, centre + textSize / 2 + textSize / 2 + textSize / 2, paint); //画出进度百分比
        }

        /**
         * 画圆弧 ，画圆环的进度
         */

        //设置进度是实心还是空心
        paint.setStrokeWidth(roundWidth); //设置圆环的宽度
        paint.setColor(roundProgressColor);  //设置进度的颜色
        RectF oval = new RectF(centre - radius, centre - radius, centre
                + radius, centre + radius);  //用于定义的圆弧的形状和大小的界限

        switch (style) {
            case STROKE: {
                paint.setStyle(Paint.Style.STROKE);
                canvas.drawArc(oval, -90, 360 * progress / max, false, paint);  //根据进度画圆弧
                break;
            }
            case FILL: {
                paint.setStyle(Paint.Style.FILL_AND_STROKE);
                if (progress != 0)
                    canvas.drawArc(oval, -90, 360 * progress / max, true, paint);  //根据进度画圆弧
                break;
            }
        }

        //圆弧2
        paint.setStrokeWidth(roundWidth2); //设置圆环的宽度
        paint.setColor(roundProgressColor2);  //设置进度的颜色
        RectF oval2 = new RectF(centre - radius2, centre - radius2, centre
                + radius2, centre + radius2);  //用于定义的圆弧的形状和大小的界限
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawArc(oval2, -90, 360 * progress2 / max, false, paint);  //根据进度画圆弧

        //圆弧3
        paint.setStrokeWidth(roundWidth3); //设置圆环的宽度
        paint.setColor(roundProgressColor3);  //设置进度的颜色
        RectF oval3 = new RectF(centre - radius3, centre - radius3, centre
                + radius3, centre + radius3);  //用于定义的圆弧的形状和大小的界限
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawArc(oval3, -90, 360 * progress2 / max, false, paint);  //根据进度画圆弧

        //圆弧4
        paint.setStrokeWidth(roundWidth4); //设置圆环的宽度
        paint.setColor(roundProgressColor4);  //设置进度的颜色
        RectF oval4 = new RectF(centre - radius4, centre - radius4, centre
                + radius4, centre + radius4);  //用于定义的圆弧的形状和大小的界限
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawArc(oval4, -90, 360 * progress2 / max, false, paint);  //根据进度画圆弧

    }


    public synchronized int getMax() {
        return max;
    }

    /**
     * 设置进度的最大值
     *
     * @param max
     */
    public synchronized void setMax(int max) {
        if (max < 0) {
            throw new IllegalArgumentException("max not less than 0");
        }
        this.max = max;
    }

    /**
     * 获取进度.需要同步
     *
     * @return
     */
    public synchronized int getProgress() {
        return progress;
    }

    /**
     * 设置进度，此为线程安全控件，由于考虑多线的问题，需要同步
     * 刷新界面调用postInvalidate()能在非UI线程刷新
     *
     * @param progress
     */
    public synchronized void setProgress(int progress) {
        if (progress < 0) {
            throw new IllegalArgumentException("progress not less than 0");
        }
        if (progress > max) {
            progress = max;
        }
        if (progress <= max) {
            this.progress = progress;
            postInvalidate();
        }
    }

    /**
     * 获取进度.需要同步
     *
     * @return
     */
    public synchronized int getProgress2() {
        return progress2;
    }

    /**
     * 设置进度，此为线程安全控件，由于考虑多线的问题，需要同步
     * 刷新界面调用postInvalidate()能在非UI线程刷新
     *
     * @param progress2
     */
    public synchronized void setProgress2(int progress2) {
        if (progress2 < 0) {
            throw new IllegalArgumentException("progress2 not less than 0");
        }
        if (progress2 > max) {
            progress2 = max;
        }
        if (progress2 <= max) {
            this.progress2 = progress2;
            postInvalidate();
        }
    }


    public int getCricleColor() {
        return roundColor;
    }

    public void setCricleColor(int cricleColor) {
        this.roundColor = cricleColor;
    }

    //内圆环颜色
    public int getCricleColor2() {
        return roundColor2;
    }

    public void setCricleColor2(int cricleColor2) {
        this.roundColor2 = cricleColor2;
    }


    public int getCricleProgressColor() {
        return roundProgressColor;
    }

    public void setCricleProgressColor(int cricleProgressColor) {
        this.roundProgressColor = cricleProgressColor;
    }

    //内进度条颜色
    public int getCricleProgressColor2() {
        return roundProgressColor2;
    }

    public void setCricleProgressColor2(int cricleProgressColor2) {
        this.roundProgressColor2 = cricleProgressColor2;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public float getTextSize() {
        return textSize;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }

    public float getRoundWidth() {
        return roundWidth;
    }

    public void setRoundWidth(float roundWidth) {
        this.roundWidth = roundWidth;
    }

    //内部圆环宽度
    public float getRoundWidth2() {
        return roundWidth2;
    }

    public void setRoundWidth2(float roundWidth2) {
        this.roundWidth2 = roundWidth2;
    }

    //内部文字设置
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}

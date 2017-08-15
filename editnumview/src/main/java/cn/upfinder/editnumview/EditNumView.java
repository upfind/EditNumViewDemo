package cn.upfinder.editnumview;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * Created by ucm on 2017/8/15.
 */

public class EditNumView extends LinearLayout implements View.OnClickListener, TextWatcher {


    private int maxNum = Integer.MAX_VALUE; //最大值
    private int inputValue = 1; //输入数量
    private int inventory = Integer.MAX_VALUE; //商品库存
    private int minNum = 1; //最小值
    private int stemNum = 1; //步长 每次增加或减少数量

    private Context context;

    private EditText etInput;
    private ImageView ivPlus;
    private ImageView ivMinus;

    private OnWarnListener onWarnListener;


    public EditNumView(Context context) {
        this(context, null);
    }

    public EditNumView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);

    }

    public EditNumView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        this.context = context;
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.EditNumView);
            boolean editable = typedArray.getBoolean(R.styleable.EditNumView_editable, true);
            int location = typedArray.getInt(R.styleable.EditNumView_location, 0);
            //左右两边宽度
            int imageWidth = typedArray.getDimensionPixelOffset(R.styleable.EditNumView_button_width, -1);
            //中间内容框宽度
            int contentWidth = typedArray.getDimensionPixelOffset(R.styleable.EditNumView_content_width, -1);
            //中间字体大小
            int contentTextSize = typedArray.getDimensionPixelSize(R.styleable.EditNumView_contentTextSize, dp2px(12));
            //中间字体颜色
            int contentTextColor = typedArray.getColor(R.styleable.EditNumView_contentTextColor, 0xff000000);
            //整个控件的背景
            Drawable background = typedArray.getDrawable(R.styleable.EditNumView_allBackground);
            //左边背景
            Drawable leftBackground = typedArray.getDrawable(R.styleable.EditNumView_leftBackground);
            //右边背景
            Drawable rightBackground = typedArray.getDrawable(R.styleable.EditNumView_rightBackground);
            //中间背景
            Drawable contentBackground = typedArray.getDrawable(R.styleable.EditNumView_contentBackground);
            //左边控件资源
            Drawable leftResources = typedArray.getDrawable(R.styleable.EditNumView_leftResources);
            //右边控件资源
            Drawable rightResources = typedArray.getDrawable(R.styleable.EditNumView_rightResources);

            //资源回收
            typedArray.recycle();

            if (location == 1) {
                //引入布局 左
                LayoutInflater.from(context).inflate(R.layout.editnumview_start_layout, this);
            } else if (location == 2) {
                //引入布局 右
                LayoutInflater.from(context).inflate(R.layout.editnumview_end_layout, this);
            } else if (location == 0) {
                //引入布局 默认
                LayoutInflater.from(context).inflate(R.layout.editnumview_layout, this);
            }

            ivMinus = findViewById(R.id.ivMinus);
            ivPlus = findViewById(R.id.ivPlus);
            etInput = findViewById(R.id.etInput);

            ivMinus.setOnClickListener(this);
            ivPlus.setOnClickListener(this);
            etInput.setOnClickListener(this);

            setEditable(editable);
            etInput.setTextColor(contentTextColor);

            //设置两边按钮宽度
            if (imageWidth > 0) {
                LayoutParams btnParams = new LayoutParams(imageWidth, LayoutParams.MATCH_PARENT);
                ivPlus.setLayoutParams(btnParams);
                ivMinus.setLayoutParams(btnParams);
            }

            //设置中间输入框宽度
            if (contentWidth > 0) {
                LayoutParams textParams = new LayoutParams(contentWidth, LayoutParams.MATCH_PARENT);
                etInput.setLayoutParams(textParams);
            }

            etInput.setTextSize(contentTextSize);
            etInput.setTextColor(contentTextColor);

            if (background != null) {
                setBackgroundDrawable(background);
            } else {
                setBackgroundResource(R.drawable.editnumview_add_sub_bg);
            }

            if (contentBackground != null) {
                etInput.setBackground(contentBackground);
            }

            if (leftBackground != null) {
                ivMinus.setBackground(leftBackground);
            }
            if (rightBackground != null) {
                ivPlus.setBackground(rightBackground);
            }
            if (leftResources != null) {
                ivMinus.setImageDrawable(leftResources);
            }
            if (rightResources != null) {
                ivPlus.setImageDrawable(rightResources);
            }


        }
    }

    /*设置是否可以输入*/
    private void setEditable(boolean editable) {
        if (editable) {
            etInput.setFocusable(true);
            etInput.setKeyListener(new DigitsKeyListener());
        } else {
            etInput.setFocusable(false);
            etInput.setKeyListener(null);
        }
    }


    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {

        onNumberInput();
    }

    /*监听输入数据变化*/
    private void onNumberInput() {
        //当前数量
        int count = getNumber();
        if (count < minNum) {
            etInput.setText(minNum + "");
            return;
        }
        int limit = Math.min(maxNum, inventory);
        if (count > limit) {
            if (inventory < maxNum) {
                if (onWarnListener != null) {
                    onWarnListener.onWarningForIventory(inventory);
                }
            } else {
                if (onWarnListener != null) {
                    onWarnListener.onWarningForMax(maxNum);
                }
            }
            etInput.setText(limit);
        } else {
            inputValue = count;
        }

    }

    /*设置相关*/
    public EditNumView setCurrentNumber(int currentNumber) {
        if (currentNumber < minNum) {
            inputValue = minNum;
        } else {
            inputValue = Math.min(Math.min(maxNum, inventory), currentNumber);
        }
        etInput.setText(inputValue + "");
        return this;
    }

    //获取库存值
    public int getInventory() {
        return inventory;
    }

    //设置库存值
    public EditNumView setInventory() {
        this.inventory = inventory;
        return this;
    }

    //获取最大值设置
    public int getMaxNum() {
        return maxNum;
    }

    //设置最大值
    public EditNumView setMaxNum(int maxNum) {
        this.maxNum = maxNum;
        return this;
    }

    //设置最小值
    public EditNumView setMinNum(int minNum) {
        this.minNum = minNum;
        return this;
    }

    //获取修改步值
    public int getStemNum() {
        return stemNum;
    }

    //设置步值
    public EditNumView setStemNum(int stemNum) {
        this.stemNum = stemNum;
        return this;
    }

    /*获取输入框中数字*/
    public int getNumber() {
        try {
            return Integer.parseInt(etInput.getText().toString());
        } catch (NumberFormatException e) {
            etInput.setText(minNum + "");
            return minNum;
        }
    }

    //设置warn监听
    public EditNumView setOnWarnListener(OnWarnListener onWarnListener) {
        this.onWarnListener = onWarnListener;
        return this;
    }

    @Override
    public void onClick(View view) {

        int id = view.getId();
        if (id == R.id.ivMinus) {
            //减
            if (inputValue > minNum) {
                inputValue -= stemNum;
                etInput.setText(inputValue + "");
            } else {
                if (onWarnListener != null) {
                    onWarnListener.onWarningForMin(minNum);
                }
            }


        } else if (id == R.id.ivPlus) {
            //加
            if (inputValue < Math.min(maxNum, inventory)) {
                inputValue += stemNum;
                etInput.setText(inputValue + "");
            } else if (inputValue < maxNum) {
                if (onWarnListener != null) {
                    onWarnListener.onWarningForIventory(inventory);
                }
            } else {
                if (onWarnListener != null) {
                    onWarnListener.onWarningForMax(maxNum);
                }
            }

        } else if (id == R.id.etInput) {
            //输入框
        }
    }


    public interface OnWarnListener {
        void onWarningForIventory(int inventory);

        void onWarningForMax(int max);

        void onWarningForMin(int min);
    }


    /*
         * dp转换成px
         * */
    public int dp2px(float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}

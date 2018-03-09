/**
 *
 */
package com.ktm.ab.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ktm.ab.R;


public class IQBaseDialog extends DialogFragment implements OnClickListener {

    protected int mDialogType = -1;
    protected Object mDialogData;
    protected View mView;
    protected FrameLayout contentHolder;
    protected RelativeLayout headerLayout;
    protected TextView headerText;
    //	protected LinearLayout bottomHolder;
    protected Button cancelBtn;
    protected IQDialogListener iQDialogListener;
    protected Context mContext;
    protected Button okBtn;
    private int currentButtonStyle;
    private boolean selfDismissable = true;

    /**
     * Constructor function
     */
    public IQBaseDialog() {
    }

    /*
     * (non-Javadoc)
     * @see android.support.v4.app.DialogFragment#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Pick a style based on the num.
        int style = DialogFragment.STYLE_NO_TITLE, theme = android.R.style.Theme_Translucent_NoTitleBar;
        setStyle(style, theme);
    }

    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onViewCreated(android.view.View, android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        super.onViewCreated(view, savedInstanceState);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
     * android.view.ViewGroup, android.os.Bundle)
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.basedialog_layout, container);
        headerLayout = (RelativeLayout) mView
                .findViewById(R.id.basedialog_header);
        headerText = (TextView) mView.findViewById(R.id.basedialog_headertext);
        contentHolder = (FrameLayout) mView
                .findViewById(R.id.basedialog_contentholder);

        okBtn = (Button) mView.findViewById(R.id.basedialog_ok);
        okBtn.setOnClickListener(this);

        cancelBtn = (Button) mView.findViewById(R.id.basedialog_cancel);
        cancelBtn.setOnClickListener(this);

        return mView;
    }

    public Object getmDialogData() {
        return mDialogData;
    }

    public void setmDialogData(Object mDialogData) {
        this.mDialogData = mDialogData;
    }


    /**
     * @param label
     * @param visibility function which will set the label and visiblity for the cancelBtn
     */
    public void setCancelBtn(String label, int visibility) {
        cancelBtn.setVisibility(visibility);
    }

    /*
     * (non-Javadoc)
     *
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.basedialog_ok:
                if (iQDialogListener != null) {
                    iQDialogListener.onIQDialogOk(mDialogType, mDialogData);
                }
                break;
            case R.id.basedialog_cancel:
                if (iQDialogListener != null)
                    iQDialogListener.onIQDialogCancel(mDialogType, false);

                break;

            default:
                break;
        }

        if (isVisible() && isSelfDismissable()) {
            this.dismiss();
        }


    }


    /**
     * @param contentView of type View
     * @return of type null
     * function which will add the content to mView
     */
    public void addContent(View contentView) {
        ((FrameLayout) mView.findViewById(R.id.basedialog_contentholder)).addView(contentView);
    }

    /**
     * @param contentView
     * @param params
     * @return of type null
     * function which will add the content to mView
     */
    public void addContent(View contentView, LayoutParams params) {
        ((FrameLayout) mView.findViewById(R.id.basedialog_contentholder))
                .addView(contentView, params);

    }

    /**
     * @param header of type String
     * @return of type null
     * function which will set the text for the headerText
     */
    public void setHeaderText(String header) {
        headerText.setText(header);
    }

    /**
     * @return iQDialogListener of type IQDialogListener
     * getter function for iQDialogListener
     */
    public IQDialogListener getiQDialogListener() {
        return iQDialogListener;
    }

    /**
     * @param iQDialogListener of type IQDialogListener
     * @return of type null
     * setter function for iQDialogListener
     */
    public void setiQDialogListener(IQDialogListener iQDialogListener) {
        this.iQDialogListener = iQDialogListener;
    }

    /**
     * @param dialogType of type int
     * @return of type null
     * setter function for mDialogType
     */
    public void setIQDialogType(int dialogType) {
        mDialogType = dialogType;
    }

    /**
     * Method to change the button style based on the dialog type
     *
     * @param buttonStyleType Integer value determining which type of dialog this is
     */
    public void setButtonStyleType(int buttonStyleType) {
        currentButtonStyle = buttonStyleType;

    }

    /*
     * (non-Javadoc)
     * @see android.support.v4.app.DialogFragment#onCancel(android.content.DialogInterface)
     */
    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        removefromBackStack();
        if (iQDialogListener != null) {
            iQDialogListener.onIQDialogCancel(mDialogType, true);
        }
    }

    /**
     * @return selfDismissable of type boolean
     * getter function for selfDismissable
     */
    public boolean isSelfDismissable() {
        return selfDismissable;
    }

    /**
     * @param selfDismissable of type boolean
     * @return of type null
     * setter function for selfDismissable
     */
    public void setSelfDismissable(boolean selfDismissable) {
        this.selfDismissable = selfDismissable;
    }


    /**
     * @return of type null
     * function which will remove the dialog from the back stack
     * @since 3 Jan 2012
     */
    protected void removefromBackStack() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag(getTag());
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
    }

    /*
     * (non-Javadoc)
     * @see android.support.v4.app.DialogFragment#show(android.support.v4.app.FragmentManager, java.lang.String)
     */
    @Override
    public void show(FragmentManager manager, String tag) {
        super.show(manager, tag);
    }


}

/**
 *
 */
package com.ktm.ab.dialog;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ktm.ab.R;

public class IQTextMessageDialog extends IQBaseDialog {

    private View layoutView;
    private String message;

    private boolean showCancelBtn;
    private boolean isHTML;
    private String headerLabel;


    private String okLabel;
    private String cancelLabel;

    public IQTextMessageDialog() {
    }

    public void init(String message) {
        this.message = message;
        mDialogType = DialogType.TEXTMESSAGE;
    }

    public void init(String message, boolean showCancelBtn) {

        this.mDialogType = DialogType.TEXTMESSAGE;
        this.showCancelBtn = showCancelBtn;
        init(message);
    }

    public void init(int dialogType, String message, boolean isHTML,
                     String okLabel, boolean showCancelBtn, String cancelLabel) {

        this.mDialogType = dialogType;
        this.okLabel = okLabel;
        this.isHTML = isHTML;
        this.showCancelBtn = showCancelBtn;
        this.cancelLabel = cancelLabel;
        this.message = message;
    }

    public void init(int dialogType, String headerLabel, String message,
                     boolean isHTML, String okLabel, boolean showCancelBtn,
                     String cancelLabel) {

        this.mDialogType = dialogType;
        this.okLabel = okLabel;
        this.isHTML = isHTML;
        this.showCancelBtn = showCancelBtn;
        this.cancelLabel = cancelLabel;
        this.message = message;
        this.headerLabel = headerLabel;
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
        super.onCreateView(inflater, container, savedInstanceState);
        if (headerLabel != null && !headerLabel.equals("")) {
            headerText.setText(headerLabel);
            headerLayout.setVisibility(View.VISIBLE);
        } else
            headerLayout.setVisibility(View.GONE);

        layoutView = inflater.inflate(R.layout.dialog_alert_layout,
                contentHolder);

        if (okLabel == null) okLabel = getString(R.string.button_ok);
        if (cancelLabel == null) cancelLabel = getString(R.string.button_cancel);

        if (!okLabel.equals(""))
            okBtn.setText(okLabel);
        if (!showCancelBtn) {
            cancelBtn.setVisibility(View.GONE);
        } else {
            if (!cancelLabel.equals("")) {
                cancelBtn.setText(cancelLabel);
            }
        }
        if (!isHTML)
            ((TextView) layoutView.findViewById(R.id.alertdialog_text))
                    .setText(message);
        else
            ((TextView) layoutView.findViewById(R.id.alertdialog_text))
                    .setText(Html.fromHtml(message));
        return mView;
    }
}

/**
 *
 */
package com.ktm.ab.dialog;

/**
 * @author Bala K J
 * @date Oct 3, 2012
 * @company Inkoniq IT Solutions
 */
public interface IQDialogListener {
    /*
     * Method called when any selection in the dialog is done
     * @param dialogType An Integer which specifies the dialogType
     * @param dialogData An Object of the data which is passed back from the Dialog
     */
    public void onIQDialogSelection(int mDialogType, Object mDialogData);

    /*
     * Method called when Ok is called
     * @param dialogType An Integer which specifies the dialogType
     * @param dialogData An Object of the data which is passed back from the Dialog
     */
    public void onIQDialogOk(int mDialogType, Object mDialogData);

    /*
     * Method called when Cancel is called
     * @param dialogType An Integer which specifies the dialogType
     */
    public void onIQDialogCancel(int mDialogType, boolean onBack);

}
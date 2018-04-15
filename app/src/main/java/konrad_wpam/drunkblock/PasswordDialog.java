package konrad_wpam.drunkblock;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class PasswordDialog extends DialogFragment
{
    public interface PasswordDialogListener
    {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }

    public PasswordDialogListener mPD_Listener;

    @Override
    public void onAttach(Context context) {

        super.onAttach(context);
        try
        {
            mPD_Listener = (PasswordDialogListener) context;
        }
        catch(ClassCastException e)
        {
            throw new ClassCastException(context.toString()
                    + " must implemexnt PasswordDialogListener");
        }
    }

    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View mView = getLayoutInflater().inflate(R.layout.password_window,null);

        EditText password = (EditText) mView.findViewById(R.id.pw_password_field);
        Button ok = (Button) mView.findViewById(R.id.pw_ok);
        Button cancel = (Button) mView.findViewById(R.id.pw_cancel);

        cancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view){
                mPD_Listener.onDialogNegativeClick(PasswordDialog.this);
            }
        });

        ok.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view){
                mPD_Listener.onDialogPositiveClick(PasswordDialog.this);
            }
        });

        builder.setView(mView);
        return builder.create();
    }
}

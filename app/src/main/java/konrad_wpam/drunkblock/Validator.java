package konrad_wpam.drunkblock;

public class Validator {


    public static boolean validateSettingsPassword(boolean isChecked, String password)
    {
        if(!isChecked) return true;
        else if(!password.equals("")) return true;
        else return false;
    }

    public static boolean validateFilledPassword(String passwordInput, String passwordSet, int[] passwordSignsOrder)
    {
        if(passwordInput.length()==passwordSet.length())
        {
            for (int i = 0; i < passwordSet.length(); i++) {
                //  System.out.println(i + " IN: " + passwordInput.charAt(i) + " || " + passwordSet.charAt(passwordSignsOrder[i] - 1));
                if (passwordInput.charAt(i) != passwordSet.charAt(passwordSignsOrder[i] - 1)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public static boolean validatePhoneNumber(String phoneNo) {
        if(phoneNo.equals("")) return true; // letting in no number
        //validate phone numbers of format "1234567890" + polski + polski z miedzynarodowym
        else if (phoneNo.matches("\\d{10}") || phoneNo.matches("\\d{9}") || phoneNo.matches("\\d{11}")) return true;
            //validating phone number with -, . or spaces
        else if(phoneNo.matches("\\d{3}[-\\.\\s]\\d{3}[-\\.\\s]\\d{4}")) return true;
            //validating phone number with extension length from 3 to 5
        else if(phoneNo.matches("\\d{3}-\\d{3}-\\d{4}\\s(x|(ext))\\d{3,5}")) return true;
            //validating phone number where area code is in braces ()
        else if(phoneNo.matches("\\(\\d{3}\\)-\\d{3}-\\d{4}")) return true;
            //return false if nothing matches the input
        else return false;
    }


}

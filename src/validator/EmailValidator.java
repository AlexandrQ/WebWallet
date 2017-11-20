package validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.validator.FacesValidator;

@FacesValidator("emailValidator")
public class EmailValidator
{
    private static final String EMAIL_PATTERN = 
        "^[_A-Za-z0-9-]+(\\." +
        "[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*" +
        "(\\.[A-Za-z]{2,})$";

    private Pattern pattern;

    public EmailValidator()
    {
        pattern = Pattern.compile(EMAIL_PATTERN);
    }

    
    public boolean validate(String str)
    {
        Matcher matcher = pattern.matcher(str.toString());
        if (!matcher.matches()) {
            return false;
        }
        else return true;
    }
}

package user;

import java.util.Date;

public abstract class User {

    public abstract String getUserName();

    public abstract void setUserName(String userName);

    public abstract String getUserId();

    public abstract void setUserId(String userId);

    public abstract String getFirstName();

    public abstract void setFirstName(String firstName);

    public abstract String getLastName();

    public abstract void setLastName(String lastName);

    public abstract Date getBirthDate();

    public abstract void setBirthDate(Date birthDate);
}

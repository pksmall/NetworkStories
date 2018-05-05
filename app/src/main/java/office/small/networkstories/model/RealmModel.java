package office.small.networkstories.model;

import io.realm.RealmObject;

public class RealmModel extends RealmObject {
    private String login;
    private String userId;
    private String avatarUrl;
    public RealmModel(){}

    public String getLogin() {
        return login;
    }

    public String getUserId() {
        return userId;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}

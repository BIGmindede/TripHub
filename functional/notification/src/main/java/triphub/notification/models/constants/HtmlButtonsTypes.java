package triphub.notification.models.constants;

public enum HtmlButtonsTypes {
    ACCEPT("ACCEPT"),
    DECLINE("DECLINE");

    public final String label;

    private HtmlButtonsTypes(String label) {
        this.label = label;
    }
}

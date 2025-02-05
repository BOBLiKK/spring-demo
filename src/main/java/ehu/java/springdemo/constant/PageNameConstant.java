package ehu.java.springdemo.constant;

public final class PageNameConstant {

    private PageNameConstant() {}

    //pages
    public static final String INDEX_PAGE = "index";
    public static final String LOGIN_PAGE = "login";
    public static final String REGISTER_PAGE = "register";
    public static final String USER_DASHBOARD_PAGE = "user/user_dashboard";
    public static final String USER_REQUEST_FORM_PAGE = "user/request-form";
    public static final String USER_MY_REQUESTS_PAGE = "user/my_requests";
    public static final String USER_CRIMINALS_PAGE = "user/criminals";
    public static final String ADMIN_DASHBOARD_PAGE = "admin/admin_dashboard";
    public static final String ADMIN_CRIMINALS_PAGE = "admin/criminals";
    public static final String ADMIN_CRIMINAL_FORM_PAGE = "admin/criminal-form";
    public static final String ADMIN_REQUESTS_PAGE =  "admin/requests";

    //redirects
    public static final String REDIRECT_LOGIN = "redirect:/login";
    public static final String REDIRECT_LOGOUT = "redirect:/logout";
    public static final String REDIRECT_USER_DASHBOARD ="redirect:/user/user_dashboard";
    public static final String REDIRECT_SAVE_USER_ERROR = "redirect:/user/create_request?error";
    public static final String REDIRECT_CRIMINAL_UPDATED_SUCCESSFULLY = "redirect:/admin/criminals?success=CriminalUpdated";
    public static final String REDIRECT_ADMIN_CRIMINALS = "redirect:/admin/criminals";
    public static final String REDIRECT_ADMIN_REQUESTS = "redirect:/admin/requests";
    public static final String REDIRECT_ADMIN_DASHBOARD = "redirect:/admin/admin_dashboard";
    public static final String REDIRECT = "redirect:/";
}

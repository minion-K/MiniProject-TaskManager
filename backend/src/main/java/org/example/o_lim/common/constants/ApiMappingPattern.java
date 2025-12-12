package org.example.o_lim.common.constants;

public class ApiMappingPattern {
    public static final String API = "/api";
    public static final String V1 = "/v1";
    public static final String BASE = API + V1;

    public static final class Users {
        private Users() {}

        public static final String ROOT = BASE + "/users";

        public static final String MY_INFO = "/me";
        public static final String MINI_MY_INFO = "/me/mini";
    }

    public static final class Auth {
        private Auth() {}

        public static final String ROOT = BASE + "/auth";

        public static final String SIGN_UP = "/sign-up";
        public static final String SIGN_IN = "/sign-in";
        public static final String SEND_EMAIL = "/send-email";
        public static final String VERIFY = "/verify";
        public static final String FIND_ID = "/find-id";
        public static final String RESET_PASSWORD = "/reset-pw";
    }

    public static final class Admin {
        private Admin() {}

        public static final String ROOT = BASE + "/admin";

        public static final String REPLACE = "/roles/replace";
        public static final String ADD = "/roles/add";
        public static final String REMOVE = "/roles/remove";
    }

    public static final class Projects {
        private Projects() {}

        public static final String ROOT = BASE + "/projects";

        public static final String BY_ID = "/{projectId}";
        public static final String SEARCH = "/title";
        public static final String SEARCH_BY_TASK_DESC = "/task-desc";
    }

    public static final class Tasks {
        private Tasks() {}

        public static final String ROOT = BASE + "/projects/{projectId}/tasks";

        public static final String BY_ID = "/{taskId}";
        public static final String UPDATE_BY_STATUS = BY_ID + "/status";
        public static final String FILTER_CREATED_USER = "/created-user/{createdUser}";
        public static final String SEARCH = "/search";
    }

    public static final class Tags {
        private Tags() {}

        public static final String ROOT = BASE + "/projects/{projectId}/tags";

        public static final String BY_ID = "/{tagId}";
    }

    public static final class Comments {
        private Comments() {}

        public static final String ROOT = BASE + "/tasks/{taskId}/comments";

        public static final String BY_ID = "/{commentId}";
        public static final String PAGE = "/page";
    }

    public static final class Notification {
        private Notification() {}

        public static final String ROOT = BASE + "/projects/{projectId}/notifications";

        public static final String BY_ID = "/{notificationId}";
    }
}

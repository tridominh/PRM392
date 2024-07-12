package com.example.prm392.Helper;

import com.google.firebase.auth.FirebaseAuth;

public class Util {
    public static boolean checkAdminRole() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if ("tw0DqWTdwNdfEmvn3CCiuwluZqr2".equals(userId)) {
            return true;
        } else {
            return false;
        }
    }
}

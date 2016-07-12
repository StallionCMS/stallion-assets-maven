package io.stallion.assetsMaven;



import java.security.Permission;
import java.util.List;
import java.util.Map;



public class PreventExitSecurityManager extends SecurityManager {
    @Override public void checkExit(int status) {
        throw new SecurityException();
    }



    @Override
    public void checkPermission(Permission perm) {

    }
}

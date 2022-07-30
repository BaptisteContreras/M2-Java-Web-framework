package fr.univlyon1.tiw1.framework.launcher;

import java.lang.reflect.InvocationTargetException;

public class AppLauncher {

    public static void launch(Class<?> appMain){
        try {
            Class<?> webFrameworkAppClass = Class.forName(appMain.getPackageName()+".WebFrameworkApp");
            DefaultWebFrameworkApp defaultWebFrameworkApp = (DefaultWebFrameworkApp) webFrameworkAppClass.getConstructor().newInstance();
            defaultWebFrameworkApp.launch();
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}

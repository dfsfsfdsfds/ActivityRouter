package com.github.mzule.activityrouter.router;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by CaoDongping on 4/6/16.
 */
public class Routers {
    private static Set<Mapping> mappings = new HashSet<>();

    static void init() {
        if (!mappings.isEmpty()) {
            return;
        }
        try {
            Class<?> clazz = Class.forName("com.github.mzule.activityrouter.router.RouterMapping");
            clazz.getMethod("map").invoke(null);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    static void map(String format, Class<? extends Activity> activity, ExtraTypes extraTypes) {
        Mapping mapping = new Mapping(format, activity, extraTypes);
        if (mappings.contains(mapping)) {
            throw new IllegalStateException("Duplicate router register");
        }
        mappings.add(mapping);
    }

    public static void open(Context context, String url) {
        Uri uri = Uri.parse(url);
        for (Mapping mapping : mappings) {
            if (mapping.match(uri)) {
                Intent intent = new Intent(context, mapping.getActivity());
                intent.putExtras(mapping.parseExtras(url));
                context.startActivity(intent);
                break;
            }
        }
    }
}

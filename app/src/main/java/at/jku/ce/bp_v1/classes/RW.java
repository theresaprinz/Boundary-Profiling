package at.jku.ce.bp_v1.classes;

import android.content.Context;
import android.content.Intent;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

import at.jku.ce.bp_v1.activities.MainActivity;

public class RW<K, V> {

    public TreeMap<K, List<V>> readMap(Context context, String file) {
        FileInputStream fis;
        TreeMap<K, List<V>> list = new TreeMap<>();
        try {
            fis = context.openFileInput(file);
            ObjectInputStream ois = new ObjectInputStream(fis);
            list = (TreeMap<K, List<V>>) ois.readObject();
            ois.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            // Während der Entwicklung notwendig, wenn die App neu installiert wird
            context.getSharedPreferences("at.jku.ce.bp_v1", Context.MODE_PRIVATE).edit().putBoolean("FIRSTSTART", true).apply();
            Intent refresh = new Intent(context, MainActivity.class);
            context.startActivity(refresh);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public void writeMap(Context context, TreeMap<K, List<V>> list, String file) {
        FileOutputStream fos;
        try {
            fos = context.openFileOutput(file, Context.MODE_PRIVATE);
            ObjectOutputStream out = new ObjectOutputStream(fos);
            out.writeObject(list);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<V> readList(Context context, String file) {
        FileInputStream fis;
        List<V> list = new LinkedList<>();
        try {
            fis = context.openFileInput(file);
            ObjectInputStream ois = new ObjectInputStream(fis);
            list = (List<V>) ois.readObject();
            ois.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public void writeList(Context context, List<V> list, String file) {
        FileOutputStream fos;
        try {
            fos = context.openFileOutput(file, Context.MODE_PRIVATE);
            ObjectOutputStream out = new ObjectOutputStream(fos);
            out.writeObject(list);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

package com.max.reinforce.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;

/**
 * Created by Administrator on 2016/12/26.
 */

public class InputStreamRunnable extends Thread {

    private BufferedReader mInStream = null;
    private PrintStream mOutStreadm = null;

    public InputStreamRunnable(InputStream in, PrintStream out) {
        mInStream = new BufferedReader(new InputStreamReader(in));
        mOutStreadm = out;
    }

    @Override
    public void run() {
        String line = null;
        try {
            while ((line = mInStream.readLine()) != null) {
                mOutStreadm.println(line);
                Thread.sleep(1);
            }

            mInStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

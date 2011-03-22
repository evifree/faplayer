package org.stagex.danmaku;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.stagex.danmaku.helper.SystemUtility;
import org.stagex.danmaku.wrapper.VLC;
import org.stagex.danmaku.wrapper.VLM;

import android.app.Application;
import android.content.res.AssetManager;

public class Danmaku extends Application {

	private static Pattern mPatternVersion = Pattern.compile("android-(\\d+)");

	protected boolean initialize() {
		// prepare, orz
		int code = SystemUtility.getSDKVersionCode();
		if (code == 6 || code == 7)
			code = 5;
		String root = super.getCacheDir().getAbsolutePath();
		try {
			ArrayList<String> list = new ArrayList<String>();
			AssetManager asset = getAssets();
			InputStream is = asset.open("index.txt");
			InputStreamReader ir = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(ir);
			try {
				while (br.ready()) {
					String line = br.readLine();
					Matcher matcher = mPatternVersion.matcher(line);
					if (matcher.find()) {
						int value = Integer.parseInt(matcher.group(1));
						if (value > code)
							continue;
					}
					list.add(line);
				}
			} finally {
				br.close();
				ir.close();
				is.close();
			}
			for (String file : list) {
				String path = String.format("%s/%s", root, file);
				String parent = path.substring(0, path.lastIndexOf('/'));
				File test = new File(parent);
				if (!test.isDirectory()) {
					test.mkdirs();
					if (!test.isDirectory())
						return false;
				}
				test = new File(path);
				// if (test.exists())
				// continue;
				byte[] buffer = new byte[32768];
				is = asset.open(file);
				OutputStream os = new FileOutputStream(path);
				try {
					while (true) {
						int rc = is.read(buffer);
						if (rc <= 0)
							break;
						os.write(buffer, 0, rc);
					}
				} finally {
					os.close();
					is.close();
				}
			}
		} catch (IOException e) {
			return false;
		}
		// start VLC
		String libd = String.format("%s/lib", root);
		VLC.setenv("VLC_PLUGIN_PATH", libd, true);
		String conf = String.format("%s/etc/vlcrc", root);
		String aout = String.format("aout_android-%d", code);
		String vout = String.format("vout_android-%d", code);
		// XXX: --intf, --aout, --vout don't make sense here
		VLC.getInstance().create(
				new String[] { "--verbose", "3", "--no-ignore-config",
						"--config", conf, "--no-plugins-cache", "--intf",
						"notify", "--aout", aout, "--vout", vout });
		// start VLM
		VLM.getInstance().create(new String[] { "127.0.0.1", "21178" });

		return true;
	}

	protected void finalize() {
		// stop VLM
		VLM.getInstance().destroy();
		// stop VLC
		VLC.getInstance().destroy();
	}

	@Override
	public void onCreate() {
		super.onCreate();

		System.setProperty("java.net.preferIPv6Addresses", "false");

		initialize();

	}

	@Override
	public void onTerminate() {
		super.onTerminate();

		finalize();

	}

}
package org.rhino.octopus.slaver.executor;

import java.net.URL;
import java.net.URLClassLoader;

public class OctopusClassLoader extends URLClassLoader{
	

    public OctopusClassLoader(URL[] urls) {
        super(urls);
    }

    public OctopusClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    public void addJar(URL url) {
        this.addURL(url);
    }

}

package org.kie.builder.impl;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.drools.core.util.IoUtils;
import org.jboss.weld.exceptions.UnsupportedOperationException;
import org.kie.builder.GAV;
import org.kie.builder.KieProjectModel;

public class ZipKieModule extends AbstractKieModules implements InternalKieModule {
    private final File             file;
    private final KieProjectModel  kieProject;    
    private Map<String, ZipEntry> zipEntries;

    public ZipKieModule(GAV gav,
                     KieProjectModel kieProject,
                     File file) {
        super( gav );
        this.file = file;
        this.kieProject = kieProject;   
        this.zipEntries = IoUtils.buildZipFileMapEntries( file );
    }
    
    @Override
    public File getFile() {
        return this.file;
    }


    @Override
    public boolean isAvailable(String name ) {
        return this.zipEntries.containsKey( name );
    }


    @Override
    public byte[] getBytes(String name) {
        ZipEntry entry = this.zipEntries.get( name );
        if ( entry == null ) {
            return null;
        }
        
        ZipFile zipFile = null;
        byte[] bytes = null;
        try {
            zipFile = new ZipFile( file );
            bytes = IoUtils.readBytesFromInputStream(  zipFile.getInputStream( entry ) );
        } catch ( IOException e ) {
            throw new RuntimeException( "Unable to get ZipFile bytes for :  " + name + " : " + file, e );
        } finally {
            if ( zipFile != null ) {
                try {
                    zipFile.close();
                } catch ( IOException e ) {
                    throw new RuntimeException( "Unable to close ZipFile when getting bytes for :  " + name + " : " + file, e );
                }
            }
        }
        return bytes;
    }

    @Override
    public Collection<String> getFileNames() {
        return this.zipEntries.keySet();
    }

    @Override
    public KieProjectModel getKieProjectModel() {
        return kieProject;
    }

    @Override
    public byte[] getBytes() {
        throw new UnsupportedOperationException();
    }

}
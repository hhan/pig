package org.apache.pig.test;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.Job;
import org.apache.pig.ResourceStatistics;
import org.apache.pig.builtin.PigStorage;
import org.apache.pig.impl.util.Utils;

public class PigStorageWithStatistics extends PigStorage {
    private String loc = null;

    @Override
    public void setLocation(String location, Job job)
            throws IOException {
        super.setLocation(location, job);
        loc = location;
    }

    @Override
    public ResourceStatistics getStatistics(String location, Job job) throws IOException {
        ResourceStatistics stats = new ResourceStatistics();
        stats.setSizeInBytes(getInputSizeInBytes());
        return stats;
    }
    
    private Long getInputSizeInBytes() throws IOException {
        if (loc == null) {
            return 0L;
        }

        long inputBytes = 0L;
        for (String location : getPathStrings(loc)) {
            Path path = new Path(location);
            FileSystem fs = path.getFileSystem(new Configuration());
            FileStatus[] status = fs.globStatus(path);
            if (status != null) {
                for (FileStatus s : status) {
                    inputBytes += Utils.getPathLength(fs, s);
                }
            }
        }
        return inputBytes;
    }
}

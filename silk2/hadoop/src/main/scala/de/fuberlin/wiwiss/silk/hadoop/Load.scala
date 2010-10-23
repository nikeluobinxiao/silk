package de.fuberlin.wiwiss.silk.hadoop

import impl.HadoopInstanceCache
import de.fuberlin.wiwiss.silk.config.{Configuration, ConfigReader}
import de.fuberlin.wiwiss.silk.impl.DefaultImplementations
import org.apache.hadoop.fs.{FileSystem, Path}
import java.util.logging.Logger
import de.fuberlin.wiwiss.silk.linkspec.LinkSpecification
import de.fuberlin.wiwiss.silk.LoadTask

/**
 * Populates the instance cache.
 */
class Load(silkConfigPath : String, instanceCachePath : String, linkSpec : Option[String], hadoopConfig : org.apache.hadoop.conf.Configuration)
{
    private val logger = Logger.getLogger(getClass.getName)

    def apply()
    {
        DefaultImplementations.register()

        val config = loadConfig(new Path(silkConfigPath), new Path(instanceCachePath))

        val linkSpecs = linkSpec match
        {
            case Some(spec) => config.linkSpec(spec).get :: Nil
            case None => config.linkSpecs
        }

        for(linkSpec <- linkSpecs)
        {
            write(config, linkSpec, new Path(instanceCachePath))
        }
    }

    private def loadConfig(filePath : Path, instanceCachePath : Path) : Configuration =
    {
        //Create two FileSystem objects, because the config file and the instance cache might be located in different file systems
        val configFS = FileSystem.get(filePath.toUri, hadoopConfig)
        val cacheFS = FileSystem.get(instanceCachePath.toUri, hadoopConfig)

        //Copy the config file into the instance cache directory
        val inputStream = configFS.open(filePath)
        val outputStream = cacheFS.create(instanceCachePath.suffix("/config.xml"))
        try
        {
            val buffer = new Array[Byte](4096)
            var c = inputStream.read(buffer)
            while(c != -1)
            {
                outputStream.write(buffer, 0, c)
                c = inputStream.read(buffer)
            }
        }
        finally
        {
            outputStream.close()
            inputStream.close()
        }

        //Load the configuration
        val stream = configFS.open(filePath)
        try
        {
            ConfigReader.read(stream)
        }
        finally
        {
            stream.close()
        }
    }

    private def write(config : Configuration, linkSpec : LinkSpecification, instanceCachePath : Path)
    {
        val cacheFS = FileSystem.get(instanceCachePath.toUri, hadoopConfig)

        val numBlocks = linkSpec.blocking.map(_.blocks).getOrElse(1)
        val sourceCache = new HadoopInstanceCache(cacheFS, instanceCachePath.suffix("/source/" + linkSpec.id + "/"), numBlocks)
        val targetCache = new HadoopInstanceCache(cacheFS, instanceCachePath.suffix("/target/" + linkSpec.id + "/"), numBlocks)

        val loader = new LoadTask(config, linkSpec, Some(sourceCache), Some(targetCache))
        loader()
    }
}

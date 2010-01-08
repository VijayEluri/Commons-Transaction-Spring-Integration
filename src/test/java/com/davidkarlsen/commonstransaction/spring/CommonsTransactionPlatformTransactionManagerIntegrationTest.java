package com.davidkarlsen.commonstransaction.spring;

import java.io.File;
import java.io.IOException;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.commons.transaction.file.ResourceManagerException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration
public class CommonsTransactionPlatformTransactionManagerIntegrationTest
{
    @Autowired
    private TestService testService;
    
    private File storeDir;

    @Before
    public void before() throws IOException {
        storeDir = new File( SystemUtils.JAVA_IO_TMPDIR, "storedir" );
        FileUtils.cleanDirectory( storeDir );
    }
    
    @Test
    public void testCreate()
        throws ResourceManagerException
    {
        // transactionAwareFileResourceManager.createResource( "someName" );
    }
    
    @Test
    public void testRollback()
        throws IOException, ResourceManagerException
    {
        final String fileName = "someFileName";
        try
        {
            testService.writeStringToFile( "someString", fileName, new RuntimeException() );
        }
        catch ( RuntimeException e )
        {
            Assert.assertFalse( new File( storeDir, fileName ).exists() );
        }
    }
    
    @Test
    public void testWrite()
        throws Exception
    {
        final String fileName = "someExistFile";
        final String fileContents = "someString";
        testService.writeStringToFile( fileContents, fileName, null );
        File createdFile = new File( storeDir, fileName );
        Assert.assertTrue( createdFile.exists() );
        Assert.assertTrue( FileUtils.readFileToString( createdFile ).equals( fileContents ) );
    }

    @Test( expected = ResourceManagerException.class )
    public void testInvalidFileName()
        throws IOException, ResourceManagerException
    {
        testService.writeStringToFile( "", "", null );
    }


}

package com.davidkarlsen.commonstransaction.spring;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.transaction.file.FileResourceManager;
import org.apache.commons.transaction.file.ResourceManagerException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.DefaultTransactionStatus;


/**
 * 
 */
public class CommonsTransactionPlatformTransactionManagerTest
{
    private final DefaultTransactionDefinition defaultTransactionDefinition = new DefaultTransactionDefinition();

    private CommonsTransactionPlatformTransactionManager commonsTransactionPlatformTransactionManager;

    private FileResourceManager fileResourceManager;
    
    private File workDir;

    private File storeDir;

    @Before
    public void before()
        throws Exception
    {
        commonsTransactionPlatformTransactionManager = new CommonsTransactionPlatformTransactionManager();
        File tmpDir = SystemUtils.getJavaIoTmpDir();
        storeDir = new File( tmpDir, "storeDir" );
        workDir = new File( tmpDir, "workDir" );

        FileUtils.deleteDirectory( storeDir );
        FileUtils.deleteDirectory( workDir );

        FileResourceManagerFactory fileResourceManagerFactory = new FileResourceManagerFactory();
        fileResourceManagerFactory.setStoreDir( storeDir );
        fileResourceManagerFactory.setWorkDir( workDir );
        fileResourceManagerFactory.afterPropertiesSet();

        fileResourceManager = (FileResourceManager) fileResourceManagerFactory.createInstance();

        commonsTransactionPlatformTransactionManager.setFileResourceManager( fileResourceManager );
        commonsTransactionPlatformTransactionManager.afterPropertiesSet();
    }
    
    @After
    public void after() throws Exception {
        this.commonsTransactionPlatformTransactionManager.destroy();
        this.fileResourceManager.stop( FileResourceManager.SHUTDOWN_MODE_NORMAL );
    }

    @Test
    public void testCommit()
        throws ResourceManagerException
    {
        TransactionStatus transactionStatus =
            commonsTransactionPlatformTransactionManager.getTransaction( defaultTransactionDefinition );
        String fileName = "someFileName";
        fileResourceManager.createResource( ( (DefaultTransactionStatus) transactionStatus ).getTransaction(), fileName );
        commonsTransactionPlatformTransactionManager.commit( transactionStatus );

        Assert.assertTrue( new File( storeDir, fileName ).exists() );
    }
    
    @Test
    public void testRollback()
        throws ResourceManagerException
    {
        TransactionStatus transactionStatus =
            commonsTransactionPlatformTransactionManager.getTransaction( defaultTransactionDefinition );
        String fileName = "someFileName";
        fileResourceManager.createResource( ( (DefaultTransactionStatus) transactionStatus ).getTransaction(), fileName );
        commonsTransactionPlatformTransactionManager.rollback( transactionStatus );
        
        Assert.assertTrue( ! new File( storeDir, fileName ).exists() );
    }


}

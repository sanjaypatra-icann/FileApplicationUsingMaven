package com.zensar;


import org.junit.Assert;
import org.junit.Test;


/**
 * Created by SP48716 on 05-05-2017.
 */
public class TestHelloWorld {

    @Test
    public void testEmailGenerator() {

        RandomEmailGenerator obj = new RandomEmailGenerator();
        String email = obj.generate();

        Assert.assertNotNull(email);
        Assert.assertEquals(email, "feedback@yoursite.com");

    }
}
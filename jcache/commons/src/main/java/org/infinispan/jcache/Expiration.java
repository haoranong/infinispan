package org.infinispan.jcache;

import javax.cache.expiry.Duration;
import javax.cache.expiry.ExpiryPolicy;

import org.infinispan.commons.logging.LogFactory;
import org.infinispan.jcache.logging.Log;

/**
 * Utility class for expiration calculations.
 *
 * @author Galder Zamarreño
 * @since 6.0
 */
public class Expiration {

   private static final Log log =
         LogFactory.getLog(Expiration.class, Log.class);

   // Suppresses default constructor, ensuring non-instantiability.
   private Expiration(){
   }

   /**
    * Return expiry for a given cache operation. It returns null when the
    * expiry time cannot be determined, in which case clients should not update
    * expiry settings for the cached entry.
    */
   public static Duration getExpiry(ExpiryPolicy policy, Operation op) {
      if (policy == null) {
         return getDefaultDuration();
      }
      switch (op) {
         case CREATION:
            try {
               return policy.getExpiryForCreation();
            } catch (Throwable t) {
               log.getExpiryHasThrown(t);
               return getDefaultDuration();
            }
         case ACCESS:
            try {
               return policy.getExpiryForAccess();
            } catch (Throwable t) {
               log.getExpiryHasThrown(t);
               // If an exception is thrown, leave expiration untouched
               return null;
            }
         case UPDATE:
            try {
               return policy.getExpiryForUpdate();
            } catch (Throwable t) {
               log.getExpiryHasThrown(t);
               // If an exception is thrown, leave expiration untouched
               return null;
            }
         default:
            throw log.unknownExpiryOperation(op.toString());
      }
   }

   public static Duration getDefaultDuration() {
      return Duration.ETERNAL;
   }

   public enum Operation {
      CREATION, ACCESS, UPDATE
   }

}

package gov.nasa.arc.verve.common.ardor3d.framework;

import com.ardor3d.renderer.queue.RenderBucketType;

/**
 * NOTE: bucket setup occurs in Ardor3dCanvas
 */
public class VerveBucketType {

    public static final RenderBucketType getRenderBucketType(final String name) {
        return RenderBucketType.getRenderBucketType(name);
    }
    public static final RenderBucketType Inherit      = RenderBucketType.Inherit;
    
    public static final RenderBucketType PreBucket    = RenderBucketType.PreBucket;
    public static final RenderBucketType Shadow       = RenderBucketType.Shadow;
    public static final RenderBucketType Opaque       = RenderBucketType.Opaque;
    public static final RenderBucketType Transparent  = RenderBucketType.Transparent;
    public static final RenderBucketType Transparent1 = getRenderBucketType("Transparent1");
    public static final RenderBucketType Transparent2 = getRenderBucketType("Transparent2");
    public static final RenderBucketType PreOrtho     = getRenderBucketType("PreOrtho");
    public static final RenderBucketType Ortho        = RenderBucketType.Ortho;
    public static final RenderBucketType PostBucket   = RenderBucketType.PostBucket;

    public static final RenderBucketType Skip         = RenderBucketType.Skip;
}

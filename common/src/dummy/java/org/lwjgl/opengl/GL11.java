package org.lwjgl.opengl;

import java.nio.*;

/**
 * Skeleton implementation of GL11 from LWJGL2 (also compatible with 3, for the most part) to allow
 * some things to compile without pulling in all of LWJGL.
 */
public final class GL11 {

	static int noinline() {
		// this class is not actually used; we just want to generate a dynamic value so that the compiler can't inline it
		// this will result in a GETFIELD in the resulting class file instead of a LDC or SIPUSH/etc
		// the JIT will immediately get rid of the GETFIELD once it realizes the field is static final at runtime
		return (int)System.currentTimeMillis();
	}

	public static final int
		GL_ACCUM = noinline(),
		GL_LOAD = noinline(),
		GL_RETURN = noinline(),
		GL_MULT = noinline(),
		GL_ADD = noinline(),
		GL_NEVER = noinline(),
		GL_LESS = noinline(),
		GL_EQUAL = noinline(),
		GL_LEQUAL = noinline(),
		GL_GREATER = noinline(),
		GL_NOTEQUAL = noinline(),
		GL_GEQUAL = noinline(),
		GL_ALWAYS = noinline(),
		GL_CURRENT_BIT = noinline(),
		GL_POINT_BIT = noinline(),
		GL_LINE_BIT = noinline(),
		GL_POLYGON_BIT = noinline(),
		GL_POLYGON_STIPPLE_BIT = noinline(),
		GL_PIXEL_MODE_BIT = noinline(),
		GL_LIGHTING_BIT = noinline(),
		GL_FOG_BIT = noinline(),
		GL_DEPTH_BUFFER_BIT = noinline(),
		GL_ACCUM_BUFFER_BIT = noinline(),
		GL_STENCIL_BUFFER_BIT = noinline(),
		GL_VIEWPORT_BIT = noinline(),
		GL_TRANSFORM_BIT = noinline(),
		GL_ENABLE_BIT = noinline(),
		GL_COLOR_BUFFER_BIT = noinline(),
		GL_HINT_BIT = noinline(),
		GL_EVAL_BIT = noinline(),
		GL_LIST_BIT = noinline(),
		GL_TEXTURE_BIT = noinline(),
		GL_SCISSOR_BIT = noinline(),
		GL_ALL_ATTRIB_BITS = noinline(),
		GL_POINTS = noinline(),
		GL_LINES = noinline(),
		GL_LINE_LOOP = noinline(),
		GL_LINE_STRIP = noinline(),
		GL_TRIANGLES = noinline(),
		GL_TRIANGLE_STRIP = noinline(),
		GL_TRIANGLE_FAN = noinline(),
		GL_QUADS = noinline(),
		GL_QUAD_STRIP = noinline(),
		GL_POLYGON = noinline(),
		GL_ZERO = noinline(),
		GL_ONE = noinline(),
		GL_SRC_COLOR = noinline(),
		GL_ONE_MINUS_SRC_COLOR = noinline(),
		GL_SRC_ALPHA = noinline(),
		GL_ONE_MINUS_SRC_ALPHA = noinline(),
		GL_DST_ALPHA = noinline(),
		GL_ONE_MINUS_DST_ALPHA = noinline(),
		GL_DST_COLOR = noinline(),
		GL_ONE_MINUS_DST_COLOR = noinline(),
		GL_SRC_ALPHA_SATURATE = noinline(),
		GL_CONSTANT_COLOR = noinline(),
		GL_ONE_MINUS_CONSTANT_COLOR = noinline(),
		GL_CONSTANT_ALPHA = noinline(),
		GL_ONE_MINUS_CONSTANT_ALPHA = noinline(),
		GL_TRUE = noinline(),
		GL_FALSE = noinline(),
		GL_CLIP_PLANE0 = noinline(),
		GL_CLIP_PLANE1 = noinline(),
		GL_CLIP_PLANE2 = noinline(),
		GL_CLIP_PLANE3 = noinline(),
		GL_CLIP_PLANE4 = noinline(),
		GL_CLIP_PLANE5 = noinline(),
		GL_BYTE = noinline(),
		GL_UNSIGNED_BYTE = noinline(),
		GL_SHORT = noinline(),
		GL_UNSIGNED_SHORT = noinline(),
		GL_INT = noinline(),
		GL_UNSIGNED_INT = noinline(),
		GL_FLOAT = noinline(),
		GL_2_BYTES = noinline(),
		GL_3_BYTES = noinline(),
		GL_4_BYTES = noinline(),
		GL_DOUBLE = noinline(),
		GL_NONE = noinline(),
		GL_FRONT_LEFT = noinline(),
		GL_FRONT_RIGHT = noinline(),
		GL_BACK_LEFT = noinline(),
		GL_BACK_RIGHT = noinline(),
		GL_FRONT = noinline(),
		GL_BACK = noinline(),
		GL_LEFT = noinline(),
		GL_RIGHT = noinline(),
		GL_FRONT_AND_BACK = noinline(),
		GL_AUX0 = noinline(),
		GL_AUX1 = noinline(),
		GL_AUX2 = noinline(),
		GL_AUX3 = noinline(),
		GL_NO_ERROR = noinline(),
		GL_INVALID_ENUM = noinline(),
		GL_INVALID_VALUE = noinline(),
		GL_INVALID_OPERATION = noinline(),
		GL_STACK_OVERFLOW = noinline(),
		GL_STACK_UNDERFLOW = noinline(),
		GL_OUT_OF_MEMORY = noinline(),
		GL_2D = noinline(),
		GL_3D = noinline(),
		GL_3D_COLOR = noinline(),
		GL_3D_COLOR_TEXTURE = noinline(),
		GL_4D_COLOR_TEXTURE = noinline(),
		GL_PASS_THROUGH_TOKEN = noinline(),
		GL_POINT_TOKEN = noinline(),
		GL_LINE_TOKEN = noinline(),
		GL_POLYGON_TOKEN = noinline(),
		GL_BITMAP_TOKEN = noinline(),
		GL_DRAW_PIXEL_TOKEN = noinline(),
		GL_COPY_PIXEL_TOKEN = noinline(),
		GL_LINE_RESET_TOKEN = noinline(),
		GL_EXP = noinline(),
		GL_EXP2 = noinline(),
		GL_CW = noinline(),
		GL_CCW = noinline(),
		GL_COEFF = noinline(),
		GL_ORDER = noinline(),
		GL_DOMAIN = noinline(),
		GL_CURRENT_COLOR = noinline(),
		GL_CURRENT_INDEX = noinline(),
		GL_CURRENT_NORMAL = noinline(),
		GL_CURRENT_TEXTURE_COORDS = noinline(),
		GL_CURRENT_RASTER_COLOR = noinline(),
		GL_CURRENT_RASTER_INDEX = noinline(),
		GL_CURRENT_RASTER_TEXTURE_COORDS = noinline(),
		GL_CURRENT_RASTER_POSITION = noinline(),
		GL_CURRENT_RASTER_POSITION_VALID = noinline(),
		GL_CURRENT_RASTER_DISTANCE = noinline(),
		GL_POINT_SMOOTH = noinline(),
		GL_POINT_SIZE = noinline(),
		GL_POINT_SIZE_RANGE = noinline(),
		GL_POINT_SIZE_GRANULARITY = noinline(),
		GL_LINE_SMOOTH = noinline(),
		GL_LINE_WIDTH = noinline(),
		GL_LINE_WIDTH_RANGE = noinline(),
		GL_LINE_WIDTH_GRANULARITY = noinline(),
		GL_LINE_STIPPLE = noinline(),
		GL_LINE_STIPPLE_PATTERN = noinline(),
		GL_LINE_STIPPLE_REPEAT = noinline(),
		GL_LIST_MODE = noinline(),
		GL_MAX_LIST_NESTING = noinline(),
		GL_LIST_BASE = noinline(),
		GL_LIST_INDEX = noinline(),
		GL_POLYGON_MODE = noinline(),
		GL_POLYGON_SMOOTH = noinline(),
		GL_POLYGON_STIPPLE = noinline(),
		GL_EDGE_FLAG = noinline(),
		GL_CULL_FACE = noinline(),
		GL_CULL_FACE_MODE = noinline(),
		GL_FRONT_FACE = noinline(),
		GL_LIGHTING = noinline(),
		GL_LIGHT_MODEL_LOCAL_VIEWER = noinline(),
		GL_LIGHT_MODEL_TWO_SIDE = noinline(),
		GL_LIGHT_MODEL_AMBIENT = noinline(),
		GL_SHADE_MODEL = noinline(),
		GL_COLOR_MATERIAL_FACE = noinline(),
		GL_COLOR_MATERIAL_PARAMETER = noinline(),
		GL_COLOR_MATERIAL = noinline(),
		GL_FOG = noinline(),
		GL_FOG_INDEX = noinline(),
		GL_FOG_DENSITY = noinline(),
		GL_FOG_START = noinline(),
		GL_FOG_END = noinline(),
		GL_FOG_MODE = noinline(),
		GL_FOG_COLOR = noinline(),
		GL_DEPTH_RANGE = noinline(),
		GL_DEPTH_TEST = noinline(),
		GL_DEPTH_WRITEMASK = noinline(),
		GL_DEPTH_CLEAR_VALUE = noinline(),
		GL_DEPTH_FUNC = noinline(),
		GL_ACCUM_CLEAR_VALUE = noinline(),
		GL_STENCIL_TEST = noinline(),
		GL_STENCIL_CLEAR_VALUE = noinline(),
		GL_STENCIL_FUNC = noinline(),
		GL_STENCIL_VALUE_MASK = noinline(),
		GL_STENCIL_FAIL = noinline(),
		GL_STENCIL_PASS_DEPTH_FAIL = noinline(),
		GL_STENCIL_PASS_DEPTH_PASS = noinline(),
		GL_STENCIL_REF = noinline(),
		GL_STENCIL_WRITEMASK = noinline(),
		GL_MATRIX_MODE = noinline(),
		GL_NORMALIZE = noinline(),
		GL_VIEWPORT = noinline(),
		GL_MODELVIEW_STACK_DEPTH = noinline(),
		GL_PROJECTION_STACK_DEPTH = noinline(),
		GL_TEXTURE_STACK_DEPTH = noinline(),
		GL_MODELVIEW_MATRIX = noinline(),
		GL_PROJECTION_MATRIX = noinline(),
		GL_TEXTURE_MATRIX = noinline(),
		GL_ATTRIB_STACK_DEPTH = noinline(),
		GL_CLIENT_ATTRIB_STACK_DEPTH = noinline(),
		GL_ALPHA_TEST = noinline(),
		GL_ALPHA_TEST_FUNC = noinline(),
		GL_ALPHA_TEST_REF = noinline(),
		GL_DITHER = noinline(),
		GL_BLEND_DST = noinline(),
		GL_BLEND_SRC = noinline(),
		GL_BLEND = noinline(),
		GL_LOGIC_OP_MODE = noinline(),
		GL_INDEX_LOGIC_OP = noinline(),
		GL_COLOR_LOGIC_OP = noinline(),
		GL_AUX_BUFFERS = noinline(),
		GL_DRAW_BUFFER = noinline(),
		GL_READ_BUFFER = noinline(),
		GL_SCISSOR_BOX = noinline(),
		GL_SCISSOR_TEST = noinline(),
		GL_INDEX_CLEAR_VALUE = noinline(),
		GL_INDEX_WRITEMASK = noinline(),
		GL_COLOR_CLEAR_VALUE = noinline(),
		GL_COLOR_WRITEMASK = noinline(),
		GL_INDEX_MODE = noinline(),
		GL_RGBA_MODE = noinline(),
		GL_DOUBLEBUFFER = noinline(),
		GL_STEREO = noinline(),
		GL_RENDER_MODE = noinline(),
		GL_PERSPECTIVE_CORRECTION_HINT = noinline(),
		GL_POINT_SMOOTH_HINT = noinline(),
		GL_LINE_SMOOTH_HINT = noinline(),
		GL_POLYGON_SMOOTH_HINT = noinline(),
		GL_FOG_HINT = noinline(),
		GL_TEXTURE_GEN_S = noinline(),
		GL_TEXTURE_GEN_T = noinline(),
		GL_TEXTURE_GEN_R = noinline(),
		GL_TEXTURE_GEN_Q = noinline(),
		GL_PIXEL_MAP_I_TO_I = noinline(),
		GL_PIXEL_MAP_S_TO_S = noinline(),
		GL_PIXEL_MAP_I_TO_R = noinline(),
		GL_PIXEL_MAP_I_TO_G = noinline(),
		GL_PIXEL_MAP_I_TO_B = noinline(),
		GL_PIXEL_MAP_I_TO_A = noinline(),
		GL_PIXEL_MAP_R_TO_R = noinline(),
		GL_PIXEL_MAP_G_TO_G = noinline(),
		GL_PIXEL_MAP_B_TO_B = noinline(),
		GL_PIXEL_MAP_A_TO_A = noinline(),
		GL_PIXEL_MAP_I_TO_I_SIZE = noinline(),
		GL_PIXEL_MAP_S_TO_S_SIZE = noinline(),
		GL_PIXEL_MAP_I_TO_R_SIZE = noinline(),
		GL_PIXEL_MAP_I_TO_G_SIZE = noinline(),
		GL_PIXEL_MAP_I_TO_B_SIZE = noinline(),
		GL_PIXEL_MAP_I_TO_A_SIZE = noinline(),
		GL_PIXEL_MAP_R_TO_R_SIZE = noinline(),
		GL_PIXEL_MAP_G_TO_G_SIZE = noinline(),
		GL_PIXEL_MAP_B_TO_B_SIZE = noinline(),
		GL_PIXEL_MAP_A_TO_A_SIZE = noinline(),
		GL_UNPACK_SWAP_BYTES = noinline(),
		GL_UNPACK_LSB_FIRST = noinline(),
		GL_UNPACK_ROW_LENGTH = noinline(),
		GL_UNPACK_SKIP_ROWS = noinline(),
		GL_UNPACK_SKIP_PIXELS = noinline(),
		GL_UNPACK_ALIGNMENT = noinline(),
		GL_PACK_SWAP_BYTES = noinline(),
		GL_PACK_LSB_FIRST = noinline(),
		GL_PACK_ROW_LENGTH = noinline(),
		GL_PACK_SKIP_ROWS = noinline(),
		GL_PACK_SKIP_PIXELS = noinline(),
		GL_PACK_ALIGNMENT = noinline(),
		GL_MAP_COLOR = noinline(),
		GL_MAP_STENCIL = noinline(),
		GL_INDEX_SHIFT = noinline(),
		GL_INDEX_OFFSET = noinline(),
		GL_RED_SCALE = noinline(),
		GL_RED_BIAS = noinline(),
		GL_ZOOM_X = noinline(),
		GL_ZOOM_Y = noinline(),
		GL_GREEN_SCALE = noinline(),
		GL_GREEN_BIAS = noinline(),
		GL_BLUE_SCALE = noinline(),
		GL_BLUE_BIAS = noinline(),
		GL_ALPHA_SCALE = noinline(),
		GL_ALPHA_BIAS = noinline(),
		GL_DEPTH_SCALE = noinline(),
		GL_DEPTH_BIAS = noinline(),
		GL_MAX_EVAL_ORDER = noinline(),
		GL_MAX_LIGHTS = noinline(),
		GL_MAX_CLIP_PLANES = noinline(),
		GL_MAX_TEXTURE_SIZE = noinline(),
		GL_MAX_PIXEL_MAP_TABLE = noinline(),
		GL_MAX_ATTRIB_STACK_DEPTH = noinline(),
		GL_MAX_MODELVIEW_STACK_DEPTH = noinline(),
		GL_MAX_NAME_STACK_DEPTH = noinline(),
		GL_MAX_PROJECTION_STACK_DEPTH = noinline(),
		GL_MAX_TEXTURE_STACK_DEPTH = noinline(),
		GL_MAX_VIEWPORT_DIMS = noinline(),
		GL_MAX_CLIENT_ATTRIB_STACK_DEPTH = noinline(),
		GL_SUBPIXEL_BITS = noinline(),
		GL_INDEX_BITS = noinline(),
		GL_RED_BITS = noinline(),
		GL_GREEN_BITS = noinline(),
		GL_BLUE_BITS = noinline(),
		GL_ALPHA_BITS = noinline(),
		GL_DEPTH_BITS = noinline(),
		GL_STENCIL_BITS = noinline(),
		GL_ACCUM_RED_BITS = noinline(),
		GL_ACCUM_GREEN_BITS = noinline(),
		GL_ACCUM_BLUE_BITS = noinline(),
		GL_ACCUM_ALPHA_BITS = noinline(),
		GL_NAME_STACK_DEPTH = noinline(),
		GL_AUTO_NORMAL = noinline(),
		GL_MAP1_COLOR_4 = noinline(),
		GL_MAP1_INDEX = noinline(),
		GL_MAP1_NORMAL = noinline(),
		GL_MAP1_TEXTURE_COORD_1 = noinline(),
		GL_MAP1_TEXTURE_COORD_2 = noinline(),
		GL_MAP1_TEXTURE_COORD_3 = noinline(),
		GL_MAP1_TEXTURE_COORD_4 = noinline(),
		GL_MAP1_VERTEX_3 = noinline(),
		GL_MAP1_VERTEX_4 = noinline(),
		GL_MAP2_COLOR_4 = noinline(),
		GL_MAP2_INDEX = noinline(),
		GL_MAP2_NORMAL = noinline(),
		GL_MAP2_TEXTURE_COORD_1 = noinline(),
		GL_MAP2_TEXTURE_COORD_2 = noinline(),
		GL_MAP2_TEXTURE_COORD_3 = noinline(),
		GL_MAP2_TEXTURE_COORD_4 = noinline(),
		GL_MAP2_VERTEX_3 = noinline(),
		GL_MAP2_VERTEX_4 = noinline(),
		GL_MAP1_GRID_DOMAIN = noinline(),
		GL_MAP1_GRID_SEGMENTS = noinline(),
		GL_MAP2_GRID_DOMAIN = noinline(),
		GL_MAP2_GRID_SEGMENTS = noinline(),
		GL_TEXTURE_1D = noinline(),
		GL_TEXTURE_2D = noinline(),
		GL_FEEDBACK_BUFFER_POINTER = noinline(),
		GL_FEEDBACK_BUFFER_SIZE = noinline(),
		GL_FEEDBACK_BUFFER_TYPE = noinline(),
		GL_SELECTION_BUFFER_POINTER = noinline(),
		GL_SELECTION_BUFFER_SIZE = noinline(),
		GL_TEXTURE_WIDTH = noinline(),
		GL_TEXTURE_HEIGHT = noinline(),
		GL_TEXTURE_INTERNAL_FORMAT = noinline(),
		GL_TEXTURE_BORDER_COLOR = noinline(),
		GL_TEXTURE_BORDER = noinline(),
		GL_DONT_CARE = noinline(),
		GL_FASTEST = noinline(),
		GL_NICEST = noinline(),
		GL_LIGHT0 = noinline(),
		GL_LIGHT1 = noinline(),
		GL_LIGHT2 = noinline(),
		GL_LIGHT3 = noinline(),
		GL_LIGHT4 = noinline(),
		GL_LIGHT5 = noinline(),
		GL_LIGHT6 = noinline(),
		GL_LIGHT7 = noinline(),
		GL_AMBIENT = noinline(),
		GL_DIFFUSE = noinline(),
		GL_SPECULAR = noinline(),
		GL_POSITION = noinline(),
		GL_SPOT_DIRECTION = noinline(),
		GL_SPOT_EXPONENT = noinline(),
		GL_SPOT_CUTOFF = noinline(),
		GL_CONSTANT_ATTENUATION = noinline(),
		GL_LINEAR_ATTENUATION = noinline(),
		GL_QUADRATIC_ATTENUATION = noinline(),
		GL_COMPILE = noinline(),
		GL_COMPILE_AND_EXECUTE = noinline(),
		GL_CLEAR = noinline(),
		GL_AND = noinline(),
		GL_AND_REVERSE = noinline(),
		GL_COPY = noinline(),
		GL_AND_INVERTED = noinline(),
		GL_NOOP = noinline(),
		GL_XOR = noinline(),
		GL_OR = noinline(),
		GL_NOR = noinline(),
		GL_EQUIV = noinline(),
		GL_INVERT = noinline(),
		GL_OR_REVERSE = noinline(),
		GL_COPY_INVERTED = noinline(),
		GL_OR_INVERTED = noinline(),
		GL_NAND = noinline(),
		GL_SET = noinline(),
		GL_EMISSION = noinline(),
		GL_SHININESS = noinline(),
		GL_AMBIENT_AND_DIFFUSE = noinline(),
		GL_COLOR_INDEXES = noinline(),
		GL_MODELVIEW = noinline(),
		GL_PROJECTION = noinline(),
		GL_TEXTURE = noinline(),
		GL_COLOR = noinline(),
		GL_DEPTH = noinline(),
		GL_STENCIL = noinline(),
		GL_COLOR_INDEX = noinline(),
		GL_STENCIL_INDEX = noinline(),
		GL_DEPTH_COMPONENT = noinline(),
		GL_RED = noinline(),
		GL_GREEN = noinline(),
		GL_BLUE = noinline(),
		GL_ALPHA = noinline(),
		GL_RGB = noinline(),
		GL_RGBA = noinline(),
		GL_LUMINANCE = noinline(),
		GL_LUMINANCE_ALPHA = noinline(),
		GL_BITMAP = noinline(),
		GL_POINT = noinline(),
		GL_LINE = noinline(),
		GL_FILL = noinline(),
		GL_RENDER = noinline(),
		GL_FEEDBACK = noinline(),
		GL_SELECT = noinline(),
		GL_FLAT = noinline(),
		GL_SMOOTH = noinline(),
		GL_KEEP = noinline(),
		GL_REPLACE = noinline(),
		GL_INCR = noinline(),
		GL_DECR = noinline(),
		GL_VENDOR = noinline(),
		GL_RENDERER = noinline(),
		GL_VERSION = noinline(),
		GL_EXTENSIONS = noinline(),
		GL_S = noinline(),
		GL_T = noinline(),
		GL_R = noinline(),
		GL_Q = noinline(),
		GL_MODULATE = noinline(),
		GL_DECAL = noinline(),
		GL_TEXTURE_ENV_MODE = noinline(),
		GL_TEXTURE_ENV_COLOR = noinline(),
		GL_TEXTURE_ENV = noinline(),
		GL_EYE_LINEAR = noinline(),
		GL_OBJECT_LINEAR = noinline(),
		GL_SPHERE_MAP = noinline(),
		GL_TEXTURE_GEN_MODE = noinline(),
		GL_OBJECT_PLANE = noinline(),
		GL_EYE_PLANE = noinline(),
		GL_NEAREST = noinline(),
		GL_LINEAR = noinline(),
		GL_NEAREST_MIPMAP_NEAREST = noinline(),
		GL_LINEAR_MIPMAP_NEAREST = noinline(),
		GL_NEAREST_MIPMAP_LINEAR = noinline(),
		GL_LINEAR_MIPMAP_LINEAR = noinline(),
		GL_TEXTURE_MAG_FILTER = noinline(),
		GL_TEXTURE_MIN_FILTER = noinline(),
		GL_TEXTURE_WRAP_S = noinline(),
		GL_TEXTURE_WRAP_T = noinline(),
		GL_CLAMP = noinline(),
		GL_REPEAT = noinline(),
		GL_CLIENT_PIXEL_STORE_BIT = noinline(),
		GL_CLIENT_VERTEX_ARRAY_BIT = noinline(),
		GL_ALL_CLIENT_ATTRIB_BITS = noinline(),
		GL_POLYGON_OFFSET_FACTOR = noinline(),
		GL_POLYGON_OFFSET_UNITS = noinline(),
		GL_POLYGON_OFFSET_POINT = noinline(),
		GL_POLYGON_OFFSET_LINE = noinline(),
		GL_POLYGON_OFFSET_FILL = noinline(),
		GL_ALPHA4 = noinline(),
		GL_ALPHA8 = noinline(),
		GL_ALPHA12 = noinline(),
		GL_ALPHA16 = noinline(),
		GL_LUMINANCE4 = noinline(),
		GL_LUMINANCE8 = noinline(),
		GL_LUMINANCE12 = noinline(),
		GL_LUMINANCE16 = noinline(),
		GL_LUMINANCE4_ALPHA4 = noinline(),
		GL_LUMINANCE6_ALPHA2 = noinline(),
		GL_LUMINANCE8_ALPHA8 = noinline(),
		GL_LUMINANCE12_ALPHA4 = noinline(),
		GL_LUMINANCE12_ALPHA12 = noinline(),
		GL_LUMINANCE16_ALPHA16 = noinline(),
		GL_INTENSITY = noinline(),
		GL_INTENSITY4 = noinline(),
		GL_INTENSITY8 = noinline(),
		GL_INTENSITY12 = noinline(),
		GL_INTENSITY16 = noinline(),
		GL_R3_G3_B2 = noinline(),
		GL_RGB4 = noinline(),
		GL_RGB5 = noinline(),
		GL_RGB8 = noinline(),
		GL_RGB10 = noinline(),
		GL_RGB12 = noinline(),
		GL_RGB16 = noinline(),
		GL_RGBA2 = noinline(),
		GL_RGBA4 = noinline(),
		GL_RGB5_A1 = noinline(),
		GL_RGBA8 = noinline(),
		GL_RGB10_A2 = noinline(),
		GL_RGBA12 = noinline(),
		GL_RGBA16 = noinline(),
		GL_TEXTURE_RED_SIZE = noinline(),
		GL_TEXTURE_GREEN_SIZE = noinline(),
		GL_TEXTURE_BLUE_SIZE = noinline(),
		GL_TEXTURE_ALPHA_SIZE = noinline(),
		GL_TEXTURE_LUMINANCE_SIZE = noinline(),
		GL_TEXTURE_INTENSITY_SIZE = noinline(),
		GL_PROXY_TEXTURE_1D = noinline(),
		GL_PROXY_TEXTURE_2D = noinline(),
		GL_TEXTURE_PRIORITY = noinline(),
		GL_TEXTURE_RESIDENT = noinline(),
		GL_TEXTURE_BINDING_1D = noinline(),
		GL_TEXTURE_BINDING_2D = noinline(),
		GL_VERTEX_ARRAY = noinline(),
		GL_NORMAL_ARRAY = noinline(),
		GL_COLOR_ARRAY = noinline(),
		GL_INDEX_ARRAY = noinline(),
		GL_TEXTURE_COORD_ARRAY = noinline(),
		GL_EDGE_FLAG_ARRAY = noinline(),
		GL_VERTEX_ARRAY_SIZE = noinline(),
		GL_VERTEX_ARRAY_TYPE = noinline(),
		GL_VERTEX_ARRAY_STRIDE = noinline(),
		GL_NORMAL_ARRAY_TYPE = noinline(),
		GL_NORMAL_ARRAY_STRIDE = noinline(),
		GL_COLOR_ARRAY_SIZE = noinline(),
		GL_COLOR_ARRAY_TYPE = noinline(),
		GL_COLOR_ARRAY_STRIDE = noinline(),
		GL_INDEX_ARRAY_TYPE = noinline(),
		GL_INDEX_ARRAY_STRIDE = noinline(),
		GL_TEXTURE_COORD_ARRAY_SIZE = noinline(),
		GL_TEXTURE_COORD_ARRAY_TYPE = noinline(),
		GL_TEXTURE_COORD_ARRAY_STRIDE = noinline(),
		GL_EDGE_FLAG_ARRAY_STRIDE = noinline(),
		GL_VERTEX_ARRAY_POINTER = noinline(),
		GL_NORMAL_ARRAY_POINTER = noinline(),
		GL_COLOR_ARRAY_POINTER = noinline(),
		GL_INDEX_ARRAY_POINTER = noinline(),
		GL_TEXTURE_COORD_ARRAY_POINTER = noinline(),
		GL_EDGE_FLAG_ARRAY_POINTER = noinline(),
		GL_V2F = noinline(),
		GL_V3F = noinline(),
		GL_C4UB_V2F = noinline(),
		GL_C4UB_V3F = noinline(),
		GL_C3F_V3F = noinline(),
		GL_N3F_V3F = noinline(),
		GL_C4F_N3F_V3F = noinline(),
		GL_T2F_V3F = noinline(),
		GL_T4F_V4F = noinline(),
		GL_T2F_C4UB_V3F = noinline(),
		GL_T2F_C3F_V3F = noinline(),
		GL_T2F_N3F_V3F = noinline(),
		GL_T2F_C4F_N3F_V3F = noinline(),
		GL_T4F_C4F_N3F_V4F = noinline(),
		GL_LOGIC_OP = noinline(),
		GL_TEXTURE_COMPONENTS = noinline();

	private GL11() {}

	public static void glAccum(int op, float value) { throw new AbstractMethodError(); }
	public static void glAlphaFunc(int func, float ref) { throw new AbstractMethodError(); }
	public static void glClearColor(float red, float green, float blue, float alpha) { throw new AbstractMethodError(); }
	public static void glClearAccum(float red, float green, float blue, float alpha) { throw new AbstractMethodError(); }
	public static void glClear(int mask) { throw new AbstractMethodError(); }
	public static void glCallLists(ByteBuffer lists) { throw new AbstractMethodError(); }
	public static void glCallLists(IntBuffer lists) { throw new AbstractMethodError(); }
	public static void glCallLists(ShortBuffer lists) { throw new AbstractMethodError(); }
	public static void glCallList(int list) { throw new AbstractMethodError(); }
	public static void glBlendFunc(int sfactor, int dfactor) { throw new AbstractMethodError(); }
	public static void glBitmap(int width, int height, float xorig, float yorig, float xmove, float ymove, ByteBuffer bitmap) { throw new AbstractMethodError(); }
	public static void glBitmap(int width, int height, float xorig, float yorig, float xmove, float ymove, long bitmap_buffer_offset) { throw new AbstractMethodError(); }
	public static void glBindTexture(int target, int texture) { throw new AbstractMethodError(); }
	public static void glPrioritizeTextures(IntBuffer textures, FloatBuffer priorities) { throw new AbstractMethodError(); }
	public static boolean glAreTexturesResident(IntBuffer textures, ByteBuffer residences) { throw new AbstractMethodError(); }
	public static void glBegin(int mode) { throw new AbstractMethodError(); }
	public static void glEnd() { throw new AbstractMethodError(); }
	public static void glArrayElement(int i) { throw new AbstractMethodError(); }
	public static void glClearDepth(double depth) { throw new AbstractMethodError(); }
	public static void glDeleteLists(int list, int range) { throw new AbstractMethodError(); }
	public static void glDeleteTextures(IntBuffer textures) { throw new AbstractMethodError(); }
	/** Overloads glDeleteTextures. */
	public static void glDeleteTextures(int texture) { throw new AbstractMethodError(); }

	public static void glCullFace(int mode) { throw new AbstractMethodError(); }
	public static void glCopyTexSubImage2D(int target, int level, int xoffset, int yoffset, int x, int y, int width, int height) { throw new AbstractMethodError(); }
	public static void glCopyTexSubImage1D(int target, int level, int xoffset, int x, int y, int width) { throw new AbstractMethodError(); }
	public static void glCopyTexImage2D(int target, int level, int internalFormat, int x, int y, int width, int height, int border) { throw new AbstractMethodError(); }
	public static void glCopyTexImage1D(int target, int level, int internalFormat, int x, int y, int width, int border) { throw new AbstractMethodError(); }
	public static void glCopyPixels(int x, int y, int width, int height, int type) { throw new AbstractMethodError(); }
	public static void glColorPointer(int size, int stride, DoubleBuffer pointer) { throw new AbstractMethodError(); }
	public static void glColorPointer(int size, int stride, FloatBuffer pointer) { throw new AbstractMethodError(); }
	public static void glColorPointer(int size, boolean unsigned, int stride, ByteBuffer pointer) { throw new AbstractMethodError(); }
	public static void glColorPointer(int size, int type, int stride, long pointer_buffer_offset) { throw new AbstractMethodError(); }
	/** Overloads glColorPointer. */
	public static void glColorPointer(int size, int type, int stride, ByteBuffer pointer) { throw new AbstractMethodError(); }

	public static void glColorMaterial(int face, int mode) { throw new AbstractMethodError(); }
	public static void glColorMask(boolean red, boolean green, boolean blue, boolean alpha) { throw new AbstractMethodError(); }
	public static void glColor3b(byte red, byte green, byte blue) { throw new AbstractMethodError(); }
	public static void glColor3f(float red, float green, float blue) { throw new AbstractMethodError(); }
	public static void glColor3d(double red, double green, double blue) { throw new AbstractMethodError(); }
	public static void glColor3ub(byte red, byte green, byte blue) { throw new AbstractMethodError(); }
	public static void glColor4b(byte red, byte green, byte blue, byte alpha) { throw new AbstractMethodError(); }
	public static void glColor4f(float red, float green, float blue, float alpha) { throw new AbstractMethodError(); }
	public static void glColor4d(double red, double green, double blue, double alpha) { throw new AbstractMethodError(); }
	public static void glColor4ub(byte red, byte green, byte blue, byte alpha) { throw new AbstractMethodError(); }
	public static void glClipPlane(int plane, DoubleBuffer equation) { throw new AbstractMethodError(); }
	public static void glClearStencil(int s) { throw new AbstractMethodError(); }
	public static void glEvalPoint1(int i) { throw new AbstractMethodError(); }
	public static void glEvalPoint2(int i, int j) { throw new AbstractMethodError(); }
	public static void glEvalMesh1(int mode, int i1, int i2) { throw new AbstractMethodError(); }
	public static void glEvalMesh2(int mode, int i1, int i2, int j1, int j2) { throw new AbstractMethodError(); }
	public static void glEvalCoord1f(float u) { throw new AbstractMethodError(); }
	public static void glEvalCoord1d(double u) { throw new AbstractMethodError(); }
	public static void glEvalCoord2f(float u, float v) { throw new AbstractMethodError(); }
	public static void glEvalCoord2d(double u, double v) { throw new AbstractMethodError(); }
	public static void glEnableClientState(int cap) { throw new AbstractMethodError(); }
	public static void glDisableClientState(int cap) { throw new AbstractMethodError(); }
	public static void glEnable(int cap) { throw new AbstractMethodError(); }
	public static void glDisable(int cap) { throw new AbstractMethodError(); }
	public static void glEdgeFlagPointer(int stride, ByteBuffer pointer) { throw new AbstractMethodError(); }
	public static void glEdgeFlagPointer(int stride, long pointer_buffer_offset) { throw new AbstractMethodError(); }
	public static void glEdgeFlag(boolean flag) { throw new AbstractMethodError(); }
	public static void glDrawPixels(int width, int height, int format, int type, ByteBuffer pixels) { throw new AbstractMethodError(); }
	public static void glDrawPixels(int width, int height, int format, int type, IntBuffer pixels) { throw new AbstractMethodError(); }
	public static void glDrawPixels(int width, int height, int format, int type, ShortBuffer pixels) { throw new AbstractMethodError(); }
	public static void glDrawPixels(int width, int height, int format, int type, long pixels_buffer_offset) { throw new AbstractMethodError(); }
	public static void glDrawElements(int mode, ByteBuffer indices) { throw new AbstractMethodError(); }
	public static void glDrawElements(int mode, IntBuffer indices) { throw new AbstractMethodError(); }
	public static void glDrawElements(int mode, ShortBuffer indices) { throw new AbstractMethodError(); }
	public static void glDrawElements(int mode, int indices_count, int type, long indices_buffer_offset) { throw new AbstractMethodError(); }
	public static void glDrawBuffer(int mode) { throw new AbstractMethodError(); }
	public static void glDrawArrays(int mode, int first, int count) { throw new AbstractMethodError(); }
	public static void glDepthRange(double zNear, double zFar) { throw new AbstractMethodError(); }
	public static void glDepthMask(boolean flag) { throw new AbstractMethodError(); }
	public static void glDepthFunc(int func) { throw new AbstractMethodError(); }
	public static void glFeedbackBuffer(int type, FloatBuffer buffer) { throw new AbstractMethodError(); }
	public static void glGetPixelMap(int map, FloatBuffer values) { throw new AbstractMethodError(); }
	public static void glGetPixelMapfv(int map, long values_buffer_offset) { throw new AbstractMethodError(); }
	public static void glGetPixelMapu(int map, IntBuffer values) { throw new AbstractMethodError(); }
	public static void glGetPixelMapuiv(int map, long values_buffer_offset) { throw new AbstractMethodError(); }
	public static void glGetPixelMapu(int map, ShortBuffer values) { throw new AbstractMethodError(); }
	public static void glGetPixelMapusv(int map, long values_buffer_offset) { throw new AbstractMethodError(); }
	public static void glGetMaterial(int face, int pname, FloatBuffer params) { throw new AbstractMethodError(); }
	public static void glGetMaterial(int face, int pname, IntBuffer params) { throw new AbstractMethodError(); }
	public static void glGetMap(int target, int query, FloatBuffer v) { throw new AbstractMethodError(); }
	public static void glGetMap(int target, int query, DoubleBuffer v) { throw new AbstractMethodError(); }
	public static void glGetMap(int target, int query, IntBuffer v) { throw new AbstractMethodError(); }
	public static void glGetLight(int light, int pname, FloatBuffer params) { throw new AbstractMethodError(); }
	public static void glGetLight(int light, int pname, IntBuffer params) { throw new AbstractMethodError(); }
	public static int glGetError() { throw new AbstractMethodError(); }
	public static void glGetClipPlane(int plane, DoubleBuffer equation) { throw new AbstractMethodError(); }
	public static void glGetBoolean(int pname, ByteBuffer params) { throw new AbstractMethodError(); }
	/** Overloads glGetBooleanv. */
	public static boolean glGetBoolean(int pname) { throw new AbstractMethodError(); }

	public static void glGetDouble(int pname, DoubleBuffer params) { throw new AbstractMethodError(); }
	/** Overloads glGetDoublev. */
	public static double glGetDouble(int pname) { throw new AbstractMethodError(); }

	public static void glGetFloat(int pname, FloatBuffer params) { throw new AbstractMethodError(); }
	/** Overloads glGetFloatv. */
	public static float glGetFloat(int pname) { throw new AbstractMethodError(); }

	public static void glGetInteger(int pname, IntBuffer params) { throw new AbstractMethodError(); }
	/** Overloads glGetIntegerv. */
	public static int glGetInteger(int pname) { throw new AbstractMethodError(); }

	public static void glGenTextures(IntBuffer textures) { throw new AbstractMethodError(); }
	/** Overloads glGenTextures. */
	public static int glGenTextures() { throw new AbstractMethodError(); }

	public static int glGenLists(int range) { throw new AbstractMethodError(); }
	public static void glFrustum(double left, double right, double bottom, double top, double zNear, double zFar) { throw new AbstractMethodError(); }
	public static void glFrontFace(int mode) { throw new AbstractMethodError(); }
	public static void glFogf(int pname, float param) { throw new AbstractMethodError(); }
	public static void glFogi(int pname, int param) { throw new AbstractMethodError(); }
	public static void glFog(int pname, FloatBuffer params) { throw new AbstractMethodError(); }
	public static void glFog(int pname, IntBuffer params) { throw new AbstractMethodError(); }
	public static void glFlush() { throw new AbstractMethodError(); }
	public static void glFinish() { throw new AbstractMethodError(); }
	public static ByteBuffer glGetPointer(int pname, long result_size) { throw new AbstractMethodError(); }
	public static boolean glIsEnabled(int cap) { throw new AbstractMethodError(); }
	public static void glInterleavedArrays(int format, int stride, ByteBuffer pointer) { throw new AbstractMethodError(); }
	public static void glInterleavedArrays(int format, int stride, DoubleBuffer pointer) { throw new AbstractMethodError(); }
	public static void glInterleavedArrays(int format, int stride, FloatBuffer pointer) { throw new AbstractMethodError(); }
	public static void glInterleavedArrays(int format, int stride, IntBuffer pointer) { throw new AbstractMethodError(); }
	public static void glInterleavedArrays(int format, int stride, ShortBuffer pointer) { throw new AbstractMethodError(); }
	public static void glInterleavedArrays(int format, int stride, long pointer_buffer_offset) { throw new AbstractMethodError(); }
	public static void glInitNames() { throw new AbstractMethodError(); }
	public static void glHint(int target, int mode) { throw new AbstractMethodError(); }
	public static void glGetTexParameter(int target, int pname, FloatBuffer params) { throw new AbstractMethodError(); }
	/** Overloads glGetTexParameterfv. */
	public static float glGetTexParameterf(int target, int pname) { throw new AbstractMethodError(); }

	public static void glGetTexParameter(int target, int pname, IntBuffer params) { throw new AbstractMethodError(); }
	/** Overloads glGetTexParameteriv. */
	public static int glGetTexParameteri(int target, int pname) { throw new AbstractMethodError(); }

	public static void glGetTexLevelParameter(int target, int level, int pname, FloatBuffer params) { throw new AbstractMethodError(); }
	/** Overloads glGetTexLevelParameterfv. */
	public static float glGetTexLevelParameterf(int target, int level, int pname) { throw new AbstractMethodError(); }

	public static void glGetTexLevelParameter(int target, int level, int pname, IntBuffer params) { throw new AbstractMethodError(); }
	/** Overloads glGetTexLevelParameteriv. */
	public static int glGetTexLevelParameteri(int target, int level, int pname) { throw new AbstractMethodError(); }

	public static void glGetTexImage(int target, int level, int format, int type, ByteBuffer pixels) { throw new AbstractMethodError(); }
	public static void glGetTexImage(int target, int level, int format, int type, DoubleBuffer pixels) { throw new AbstractMethodError(); }
	public static void glGetTexImage(int target, int level, int format, int type, FloatBuffer pixels) { throw new AbstractMethodError(); }
	public static void glGetTexImage(int target, int level, int format, int type, IntBuffer pixels) { throw new AbstractMethodError(); }
	public static void glGetTexImage(int target, int level, int format, int type, ShortBuffer pixels) { throw new AbstractMethodError(); }
	public static void glGetTexImage(int target, int level, int format, int type, long pixels_buffer_offset) { throw new AbstractMethodError(); }
	public static void glGetTexGen(int coord, int pname, IntBuffer params) { throw new AbstractMethodError(); }
	/** Overloads glGetTexGeniv. */
	public static int glGetTexGeni(int coord, int pname) { throw new AbstractMethodError(); }

	public static void glGetTexGen(int coord, int pname, FloatBuffer params) { throw new AbstractMethodError(); }
	/** Overloads glGetTexGenfv. */
	public static float glGetTexGenf(int coord, int pname) { throw new AbstractMethodError(); }

	public static void glGetTexGen(int coord, int pname, DoubleBuffer params) { throw new AbstractMethodError(); }
	/** Overloads glGetTexGendv. */
	public static double glGetTexGend(int coord, int pname) { throw new AbstractMethodError(); }

	public static void glGetTexEnv(int coord, int pname, IntBuffer params) { throw new AbstractMethodError(); }
	/** Overloads glGetTexEnviv. */
	public static int glGetTexEnvi(int coord, int pname) { throw new AbstractMethodError(); }

	public static void glGetTexEnv(int coord, int pname, FloatBuffer params) { throw new AbstractMethodError(); }
	/** Overloads glGetTexEnvfv. */
	public static float glGetTexEnvf(int coord, int pname) { throw new AbstractMethodError(); }

	public static String glGetString(int name) { throw new AbstractMethodError(); }
	public static void glGetPolygonStipple(ByteBuffer mask) { throw new AbstractMethodError(); }
	public static void glGetPolygonStipple(long mask_buffer_offset) { throw new AbstractMethodError(); }
	public static boolean glIsList(int list) { throw new AbstractMethodError(); }
	public static void glMaterialf(int face, int pname, float param) { throw new AbstractMethodError(); }
	public static void glMateriali(int face, int pname, int param) { throw new AbstractMethodError(); }
	public static void glMaterial(int face, int pname, FloatBuffer params) { throw new AbstractMethodError(); }
	public static void glMaterial(int face, int pname, IntBuffer params) { throw new AbstractMethodError(); }
	public static void glMapGrid1f(int un, float u1, float u2) { throw new AbstractMethodError(); }
	public static void glMapGrid1d(int un, double u1, double u2) { throw new AbstractMethodError(); }
	public static void glMapGrid2f(int un, float u1, float u2, int vn, float v1, float v2) { throw new AbstractMethodError(); }
	public static void glMapGrid2d(int un, double u1, double u2, int vn, double v1, double v2) { throw new AbstractMethodError(); }
	public static void glMap2f(int target, float u1, float u2, int ustride, int uorder, float v1, float v2, int vstride, int vorder, FloatBuffer points) { throw new AbstractMethodError(); }
	public static void glMap2d(int target, double u1, double u2, int ustride, int uorder, double v1, double v2, int vstride, int vorder, DoubleBuffer points) { throw new AbstractMethodError(); }
	public static void glMap1f(int target, float u1, float u2, int stride, int order, FloatBuffer points) { throw new AbstractMethodError(); }
	public static void glMap1d(int target, double u1, double u2, int stride, int order, DoubleBuffer points) { throw new AbstractMethodError(); }
	public static void glLogicOp(int opcode) { throw new AbstractMethodError(); }
	public static void glLoadName(int name) { throw new AbstractMethodError(); }
	public static void glLoadMatrix(FloatBuffer m) { throw new AbstractMethodError(); }
	public static void glLoadMatrix(DoubleBuffer m) { throw new AbstractMethodError(); }
	public static void glLoadIdentity() { throw new AbstractMethodError(); }
	public static void glListBase(int base) { throw new AbstractMethodError(); }
	public static void glLineWidth(float width) { throw new AbstractMethodError(); }
	public static void glLineStipple(int factor, short pattern) { throw new AbstractMethodError(); }
	public static void glLightModelf(int pname, float param) { throw new AbstractMethodError(); }
	public static void glLightModeli(int pname, int param) { throw new AbstractMethodError(); }
	public static void glLightModel(int pname, FloatBuffer params) { throw new AbstractMethodError(); }
	public static void glLightModel(int pname, IntBuffer params) { throw new AbstractMethodError(); }
	public static void glLightf(int light, int pname, float param) { throw new AbstractMethodError(); }
	public static void glLighti(int light, int pname, int param) { throw new AbstractMethodError(); }
	public static void glLight(int light, int pname, FloatBuffer params) { throw new AbstractMethodError(); }
	public static void glLight(int light, int pname, IntBuffer params) { throw new AbstractMethodError(); }
	public static boolean glIsTexture(int texture) { throw new AbstractMethodError(); }
	public static void glMatrixMode(int mode) { throw new AbstractMethodError(); }
	public static void glPolygonStipple(ByteBuffer mask) { throw new AbstractMethodError(); }
	public static void glPolygonStipple(long mask_buffer_offset) { throw new AbstractMethodError(); }
	public static void glPolygonOffset(float factor, float units) { throw new AbstractMethodError(); }
	public static void glPolygonMode(int face, int mode) { throw new AbstractMethodError(); }
	public static void glPointSize(float size) { throw new AbstractMethodError(); }
	public static void glPixelZoom(float xfactor, float yfactor) { throw new AbstractMethodError(); }
	public static void glPixelTransferf(int pname, float param) { throw new AbstractMethodError(); }
	public static void glPixelTransferi(int pname, int param) { throw new AbstractMethodError(); }
	public static void glPixelStoref(int pname, float param) { throw new AbstractMethodError(); }
	public static void glPixelStorei(int pname, int param) { throw new AbstractMethodError(); }
	public static void glPixelMap(int map, FloatBuffer values) { throw new AbstractMethodError(); }
	public static void glPixelMapfv(int map, int values_mapsize, long values_buffer_offset) { throw new AbstractMethodError(); }
	public static void glPixelMapu(int map, IntBuffer values) { throw new AbstractMethodError(); }
	public static void glPixelMapuiv(int map, int values_mapsize, long values_buffer_offset) { throw new AbstractMethodError(); }
	public static void glPixelMapu(int map, ShortBuffer values) { throw new AbstractMethodError(); }
	public static void glPixelMapusv(int map, int values_mapsize, long values_buffer_offset) { throw new AbstractMethodError(); }
	public static void glPassThrough(float token) { throw new AbstractMethodError(); }
	public static void glOrtho(double left, double right, double bottom, double top, double zNear, double zFar) { throw new AbstractMethodError(); }
	public static void glNormalPointer(int stride, ByteBuffer pointer) { throw new AbstractMethodError(); }
	public static void glNormalPointer(int stride, DoubleBuffer pointer) { throw new AbstractMethodError(); }
	public static void glNormalPointer(int stride, FloatBuffer pointer) { throw new AbstractMethodError(); }
	public static void glNormalPointer(int stride, IntBuffer pointer) { throw new AbstractMethodError(); }
	public static void glNormalPointer(int type, int stride, long pointer_buffer_offset) { throw new AbstractMethodError(); }
	/** Overloads glNormalPointer. */
	public static void glNormalPointer(int type, int stride, ByteBuffer pointer) { throw new AbstractMethodError(); }

	public static void glNormal3b(byte nx, byte ny, byte nz) { throw new AbstractMethodError(); }
	public static void glNormal3f(float nx, float ny, float nz) { throw new AbstractMethodError(); }
	public static void glNormal3d(double nx, double ny, double nz) { throw new AbstractMethodError(); }
	public static void glNormal3i(int nx, int ny, int nz) { throw new AbstractMethodError(); }
	public static void glNewList(int list, int mode) { throw new AbstractMethodError(); }
	public static void glEndList() { throw new AbstractMethodError(); }
	public static void glMultMatrix(FloatBuffer m) { throw new AbstractMethodError(); }
	public static void glMultMatrix(DoubleBuffer m) { throw new AbstractMethodError(); }
	public static void glShadeModel(int mode) { throw new AbstractMethodError(); }
	public static void glSelectBuffer(IntBuffer buffer) { throw new AbstractMethodError(); }
	public static void glScissor(int x, int y, int width, int height) { throw new AbstractMethodError(); }
	public static void glScalef(float x, float y, float z) { throw new AbstractMethodError(); }
	public static void glScaled(double x, double y, double z) { throw new AbstractMethodError(); }
	public static void glRotatef(float angle, float x, float y, float z) { throw new AbstractMethodError(); }
	public static void glRotated(double angle, double x, double y, double z) { throw new AbstractMethodError(); }
	public static int glRenderMode(int mode) { throw new AbstractMethodError(); }
	public static void glRectf(float x1, float y1, float x2, float y2) { throw new AbstractMethodError(); }
	public static void glRectd(double x1, double y1, double x2, double y2) { throw new AbstractMethodError(); }
	public static void glRecti(int x1, int y1, int x2, int y2) { throw new AbstractMethodError(); }
	public static void glReadPixels(int x, int y, int width, int height, int format, int type, ByteBuffer pixels) { throw new AbstractMethodError(); }
	public static void glReadPixels(int x, int y, int width, int height, int format, int type, DoubleBuffer pixels) { throw new AbstractMethodError(); }
	public static void glReadPixels(int x, int y, int width, int height, int format, int type, FloatBuffer pixels) { throw new AbstractMethodError(); }
	public static void glReadPixels(int x, int y, int width, int height, int format, int type, IntBuffer pixels) { throw new AbstractMethodError(); }
	public static void glReadPixels(int x, int y, int width, int height, int format, int type, ShortBuffer pixels) { throw new AbstractMethodError(); }
	public static void glReadPixels(int x, int y, int width, int height, int format, int type, long pixels_buffer_offset) { throw new AbstractMethodError(); }
	public static void glReadBuffer(int mode) { throw new AbstractMethodError(); }
	public static void glRasterPos2f(float x, float y) { throw new AbstractMethodError(); }
	public static void glRasterPos2d(double x, double y) { throw new AbstractMethodError(); }
	public static void glRasterPos2i(int x, int y) { throw new AbstractMethodError(); }
	public static void glRasterPos3f(float x, float y, float z) { throw new AbstractMethodError(); }
	public static void glRasterPos3d(double x, double y, double z) { throw new AbstractMethodError(); }
	public static void glRasterPos3i(int x, int y, int z) { throw new AbstractMethodError(); }
	public static void glRasterPos4f(float x, float y, float z, float w) { throw new AbstractMethodError(); }
	public static void glRasterPos4d(double x, double y, double z, double w) { throw new AbstractMethodError(); }
	public static void glRasterPos4i(int x, int y, int z, int w) { throw new AbstractMethodError(); }
	public static void glPushName(int name) { throw new AbstractMethodError(); }
	public static void glPopName() { throw new AbstractMethodError(); }
	public static void glPushMatrix() { throw new AbstractMethodError(); }
	public static void glPopMatrix() { throw new AbstractMethodError(); }
	public static void glPushClientAttrib(int mask) { throw new AbstractMethodError(); }
	public static void glPopClientAttrib() { throw new AbstractMethodError(); }
	public static void glPushAttrib(int mask) { throw new AbstractMethodError(); }
	public static void glPopAttrib() { throw new AbstractMethodError(); }
	public static void glStencilFunc(int func, int ref, int mask) { throw new AbstractMethodError(); }
	public static void glVertexPointer(int size, int stride, DoubleBuffer pointer) { throw new AbstractMethodError(); }
	public static void glVertexPointer(int size, int stride, FloatBuffer pointer) { throw new AbstractMethodError(); }
	public static void glVertexPointer(int size, int stride, IntBuffer pointer) { throw new AbstractMethodError(); }
	public static void glVertexPointer(int size, int stride, ShortBuffer pointer) { throw new AbstractMethodError(); }
	public static void glVertexPointer(int size, int type, int stride, long pointer_buffer_offset) { throw new AbstractMethodError(); }
	/** Overloads glVertexPointer. */
	public static void glVertexPointer(int size, int type, int stride, ByteBuffer pointer) { throw new AbstractMethodError(); }

	public static void glVertex2f(float x, float y) { throw new AbstractMethodError(); }
	public static void glVertex2d(double x, double y) { throw new AbstractMethodError(); }
	public static void glVertex2i(int x, int y) { throw new AbstractMethodError(); }
	public static void glVertex3f(float x, float y, float z) { throw new AbstractMethodError(); }
	public static void glVertex3d(double x, double y, double z) { throw new AbstractMethodError(); }
	public static void glVertex3i(int x, int y, int z) { throw new AbstractMethodError(); }
	public static void glVertex4f(float x, float y, float z, float w) { throw new AbstractMethodError(); }
	public static void glVertex4d(double x, double y, double z, double w) { throw new AbstractMethodError(); }
	public static void glVertex4i(int x, int y, int z, int w) { throw new AbstractMethodError(); }
	public static void glTranslatef(float x, float y, float z) { throw new AbstractMethodError(); }
	public static void glTranslated(double x, double y, double z) { throw new AbstractMethodError(); }
	public static void glTexImage1D(int target, int level, int internalformat, int width, int border, int format, int type, ByteBuffer pixels) { throw new AbstractMethodError(); }
	public static void glTexImage1D(int target, int level, int internalformat, int width, int border, int format, int type, DoubleBuffer pixels) { throw new AbstractMethodError(); }
	public static void glTexImage1D(int target, int level, int internalformat, int width, int border, int format, int type, FloatBuffer pixels) { throw new AbstractMethodError(); }
	public static void glTexImage1D(int target, int level, int internalformat, int width, int border, int format, int type, IntBuffer pixels) { throw new AbstractMethodError(); }
	public static void glTexImage1D(int target, int level, int internalformat, int width, int border, int format, int type, ShortBuffer pixels) { throw new AbstractMethodError(); }
	public static void glTexImage1D(int target, int level, int internalformat, int width, int border, int format, int type, long pixels_buffer_offset) { throw new AbstractMethodError(); }
	public static void glTexImage2D(int target, int level, int internalformat, int width, int height, int border, int format, int type, ByteBuffer pixels) { throw new AbstractMethodError(); }
	public static void glTexImage2D(int target, int level, int internalformat, int width, int height, int border, int format, int type, DoubleBuffer pixels) { throw new AbstractMethodError(); }
	public static void glTexImage2D(int target, int level, int internalformat, int width, int height, int border, int format, int type, FloatBuffer pixels) { throw new AbstractMethodError(); }
	public static void glTexImage2D(int target, int level, int internalformat, int width, int height, int border, int format, int type, IntBuffer pixels) { throw new AbstractMethodError(); }
	public static void glTexImage2D(int target, int level, int internalformat, int width, int height, int border, int format, int type, ShortBuffer pixels) { throw new AbstractMethodError(); }
	public static void glTexImage2D(int target, int level, int internalformat, int width, int height, int border, int format, int type, long pixels_buffer_offset) { throw new AbstractMethodError(); }
	public static void glTexSubImage1D(int target, int level, int xoffset, int width, int format, int type, ByteBuffer pixels) { throw new AbstractMethodError(); }
	public static void glTexSubImage1D(int target, int level, int xoffset, int width, int format, int type, DoubleBuffer pixels) { throw new AbstractMethodError(); }
	public static void glTexSubImage1D(int target, int level, int xoffset, int width, int format, int type, FloatBuffer pixels) { throw new AbstractMethodError(); }
	public static void glTexSubImage1D(int target, int level, int xoffset, int width, int format, int type, IntBuffer pixels) { throw new AbstractMethodError(); }
	public static void glTexSubImage1D(int target, int level, int xoffset, int width, int format, int type, ShortBuffer pixels) { throw new AbstractMethodError(); }
	public static void glTexSubImage1D(int target, int level, int xoffset, int width, int format, int type, long pixels_buffer_offset) { throw new AbstractMethodError(); }
	public static void glTexSubImage2D(int target, int level, int xoffset, int yoffset, int width, int height, int format, int type, ByteBuffer pixels) { throw new AbstractMethodError(); }
	public static void glTexSubImage2D(int target, int level, int xoffset, int yoffset, int width, int height, int format, int type, DoubleBuffer pixels) { throw new AbstractMethodError(); }
	public static void glTexSubImage2D(int target, int level, int xoffset, int yoffset, int width, int height, int format, int type, FloatBuffer pixels) { throw new AbstractMethodError(); }
	public static void glTexSubImage2D(int target, int level, int xoffset, int yoffset, int width, int height, int format, int type, IntBuffer pixels) { throw new AbstractMethodError(); }
	public static void glTexSubImage2D(int target, int level, int xoffset, int yoffset, int width, int height, int format, int type, ShortBuffer pixels) { throw new AbstractMethodError(); }
	public static void glTexSubImage2D(int target, int level, int xoffset, int yoffset, int width, int height, int format, int type, long pixels_buffer_offset) { throw new AbstractMethodError(); }
	public static void glTexParameterf(int target, int pname, float param) { throw new AbstractMethodError(); }
	public static void glTexParameteri(int target, int pname, int param) { throw new AbstractMethodError(); }
	public static void glTexParameter(int target, int pname, FloatBuffer param) { throw new AbstractMethodError(); }
	public static void glTexParameter(int target, int pname, IntBuffer param) { throw new AbstractMethodError(); }
	public static void glTexGenf(int coord, int pname, float param) { throw new AbstractMethodError(); }
	public static void glTexGend(int coord, int pname, double param) { throw new AbstractMethodError(); }
	public static void glTexGen(int coord, int pname, FloatBuffer params) { throw new AbstractMethodError(); }
	public static void glTexGen(int coord, int pname, DoubleBuffer params) { throw new AbstractMethodError(); }
	public static void glTexGeni(int coord, int pname, int param) { throw new AbstractMethodError(); }
	public static void glTexGen(int coord, int pname, IntBuffer params) { throw new AbstractMethodError(); }
	public static void glTexEnvf(int target, int pname, float param) { throw new AbstractMethodError(); }
	public static void glTexEnvi(int target, int pname, int param) { throw new AbstractMethodError(); }
	public static void glTexEnv(int target, int pname, FloatBuffer params) { throw new AbstractMethodError(); }
	public static void glTexEnv(int target, int pname, IntBuffer params) { throw new AbstractMethodError(); }
	public static void glTexCoordPointer(int size, int stride, DoubleBuffer pointer) { throw new AbstractMethodError(); }
	public static void glTexCoordPointer(int size, int stride, FloatBuffer pointer) { throw new AbstractMethodError(); }
	public static void glTexCoordPointer(int size, int stride, IntBuffer pointer) { throw new AbstractMethodError(); }
	public static void glTexCoordPointer(int size, int stride, ShortBuffer pointer) { throw new AbstractMethodError(); }
	public static void glTexCoordPointer(int size, int type, int stride, long pointer_buffer_offset) { throw new AbstractMethodError(); }
	/** Overloads glTexCoordPointer. */
	public static void glTexCoordPointer(int size, int type, int stride, ByteBuffer pointer) { throw new AbstractMethodError(); }

	public static void glTexCoord1f(float s) { throw new AbstractMethodError(); }
	public static void glTexCoord1d(double s) { throw new AbstractMethodError(); }
	public static void glTexCoord2f(float s, float t) { throw new AbstractMethodError(); }
	public static void glTexCoord2d(double s, double t) { throw new AbstractMethodError(); }
	public static void glTexCoord3f(float s, float t, float r) { throw new AbstractMethodError(); }
	public static void glTexCoord3d(double s, double t, double r) { throw new AbstractMethodError(); }
	public static void glTexCoord4f(float s, float t, float r, float q) { throw new AbstractMethodError(); }
	public static void glTexCoord4d(double s, double t, double r, double q) { throw new AbstractMethodError(); }
	public static void glStencilOp(int fail, int zfail, int zpass) { throw new AbstractMethodError(); }
	public static void glStencilMask(int mask) { throw new AbstractMethodError(); }
	public static void glViewport(int x, int y, int width, int height) { throw new AbstractMethodError(); }
}

package com.jme3.system.jopenvr;
import com.sun.jna.Callback;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import java.util.Arrays;
import java.util.List;
/**
 * <i>native declaration : headers\openvr_capi.h:2251</i><br>
 * This file was autogenerated by <a href="http://jnaerator.googlecode.com/">JNAerator</a>,<br>
 * a tool written by <a href="http://ochafik.com/">Olivier Chafik</a> that <a href="http://code.google.com/p/jnaerator/wiki/CreditsAndLicense">uses a few opensource projects.</a>.<br>
 * For help, please visit <a href="http://nativelibs4java.googlecode.com/">NativeLibs4Java</a> , <a href="http://rococoa.dev.java.net/">Rococoa</a>, or <a href="http://jna.dev.java.net/">JNA</a>.
 */
public class VR_IVRRenderModels_FnTable extends Structure {
	/** C type : LoadRenderModel_Async_callback* */
	public VR_IVRRenderModels_FnTable.LoadRenderModel_Async_callback LoadRenderModel_Async;
	/** C type : FreeRenderModel_callback* */
	public VR_IVRRenderModels_FnTable.FreeRenderModel_callback FreeRenderModel;
	/** C type : LoadTexture_Async_callback* */
	public VR_IVRRenderModels_FnTable.LoadTexture_Async_callback LoadTexture_Async;
	/** C type : FreeTexture_callback* */
	public VR_IVRRenderModels_FnTable.FreeTexture_callback FreeTexture;
	/** C type : LoadTextureD3D11_Async_callback* */
	public VR_IVRRenderModels_FnTable.LoadTextureD3D11_Async_callback LoadTextureD3D11_Async;
	/** C type : LoadIntoTextureD3D11_Async_callback* */
	public VR_IVRRenderModels_FnTable.LoadIntoTextureD3D11_Async_callback LoadIntoTextureD3D11_Async;
	/** C type : FreeTextureD3D11_callback* */
	public VR_IVRRenderModels_FnTable.FreeTextureD3D11_callback FreeTextureD3D11;
	/** C type : GetRenderModelName_callback* */
	public VR_IVRRenderModels_FnTable.GetRenderModelName_callback GetRenderModelName;
	/** C type : GetRenderModelCount_callback* */
	public VR_IVRRenderModels_FnTable.GetRenderModelCount_callback GetRenderModelCount;
	/** C type : GetComponentCount_callback* */
	public VR_IVRRenderModels_FnTable.GetComponentCount_callback GetComponentCount;
	/** C type : GetComponentName_callback* */
	public VR_IVRRenderModels_FnTable.GetComponentName_callback GetComponentName;
	/** C type : GetComponentButtonMask_callback* */
	public VR_IVRRenderModels_FnTable.GetComponentButtonMask_callback GetComponentButtonMask;
	/** C type : GetComponentRenderModelName_callback* */
	public VR_IVRRenderModels_FnTable.GetComponentRenderModelName_callback GetComponentRenderModelName;
	/** C type : GetComponentStateForDevicePath_callback* */
	public VR_IVRRenderModels_FnTable.GetComponentStateForDevicePath_callback GetComponentStateForDevicePath;
	/** C type : GetComponentState_callback* */
	public VR_IVRRenderModels_FnTable.GetComponentState_callback GetComponentState;
	/** C type : RenderModelHasComponent_callback* */
	public VR_IVRRenderModels_FnTable.RenderModelHasComponent_callback RenderModelHasComponent;
	/** C type : GetRenderModelThumbnailURL_callback* */
	public VR_IVRRenderModels_FnTable.GetRenderModelThumbnailURL_callback GetRenderModelThumbnailURL;
	/** C type : GetRenderModelOriginalPath_callback* */
	public VR_IVRRenderModels_FnTable.GetRenderModelOriginalPath_callback GetRenderModelOriginalPath;
	/** C type : GetRenderModelErrorNameFromEnum_callback* */
	public VR_IVRRenderModels_FnTable.GetRenderModelErrorNameFromEnum_callback GetRenderModelErrorNameFromEnum;
	/** <i>native declaration : headers\openvr_capi.h:2232</i> */
	public interface LoadRenderModel_Async_callback extends Callback {
		int apply(Pointer pchRenderModelName, PointerByReference ppRenderModel);
	};
	/** <i>native declaration : headers\openvr_capi.h:2233</i> */
	public interface FreeRenderModel_callback extends Callback {
		void apply(RenderModel_t pRenderModel);
	};
	/** <i>native declaration : headers\openvr_capi.h:2234</i> */
	public interface LoadTexture_Async_callback extends Callback {
		int apply(int textureId, PointerByReference ppTexture);
	};
	/** <i>native declaration : headers\openvr_capi.h:2235</i> */
	public interface FreeTexture_callback extends Callback {
		void apply(RenderModel_TextureMap_t pTexture);
	};
	/** <i>native declaration : headers\openvr_capi.h:2236</i> */
	public interface LoadTextureD3D11_Async_callback extends Callback {
		int apply(int textureId, Pointer pD3D11Device, PointerByReference ppD3D11Texture2D);
	};
	/** <i>native declaration : headers\openvr_capi.h:2237</i> */
	public interface LoadIntoTextureD3D11_Async_callback extends Callback {
		int apply(int textureId, Pointer pDstTexture);
	};
	/** <i>native declaration : headers\openvr_capi.h:2238</i> */
	public interface FreeTextureD3D11_callback extends Callback {
		void apply(Pointer pD3D11Texture2D);
	};
	/** <i>native declaration : headers\openvr_capi.h:2239</i> */
	public interface GetRenderModelName_callback extends Callback {
		int apply(int unRenderModelIndex, Pointer pchRenderModelName, int unRenderModelNameLen);
	};
	/** <i>native declaration : headers\openvr_capi.h:2240</i> */
	public interface GetRenderModelCount_callback extends Callback {
		int apply();
	};
	/** <i>native declaration : headers\openvr_capi.h:2241</i> */
	public interface GetComponentCount_callback extends Callback {
		int apply(Pointer pchRenderModelName);
	};
	/** <i>native declaration : headers\openvr_capi.h:2242</i> */
	public interface GetComponentName_callback extends Callback {
		int apply(Pointer pchRenderModelName, int unComponentIndex, Pointer pchComponentName, int unComponentNameLen);
	};
	/** <i>native declaration : headers\openvr_capi.h:2243</i> */
	public interface GetComponentButtonMask_callback extends Callback {
		long apply(Pointer pchRenderModelName, Pointer pchComponentName);
	};
	/** <i>native declaration : headers\openvr_capi.h:2244</i> */
	public interface GetComponentRenderModelName_callback extends Callback {
		int apply(Pointer pchRenderModelName, Pointer pchComponentName, Pointer pchComponentRenderModelName, int unComponentRenderModelNameLen);
	};
	/** <i>native declaration : headers\openvr_capi.h:2245</i> */
	public interface GetComponentStateForDevicePath_callback extends Callback {
		byte apply(Pointer pchRenderModelName, Pointer pchComponentName, long devicePath, RenderModel_ControllerMode_State_t pState, RenderModel_ComponentState_t pComponentState);
	};
	/** <i>native declaration : headers\openvr_capi.h:2246</i> */
	public interface GetComponentState_callback extends Callback {
		byte apply(Pointer pchRenderModelName, Pointer pchComponentName, VRControllerState_t pControllerState, RenderModel_ControllerMode_State_t pState, RenderModel_ComponentState_t pComponentState);
	};
	/** <i>native declaration : headers\openvr_capi.h:2247</i> */
	public interface RenderModelHasComponent_callback extends Callback {
		byte apply(Pointer pchRenderModelName, Pointer pchComponentName);
	};
	/** <i>native declaration : headers\openvr_capi.h:2248</i> */
	public interface GetRenderModelThumbnailURL_callback extends Callback {
		int apply(Pointer pchRenderModelName, Pointer pchThumbnailURL, int unThumbnailURLLen, IntByReference peError);
	};
	/** <i>native declaration : headers\openvr_capi.h:2249</i> */
	public interface GetRenderModelOriginalPath_callback extends Callback {
		int apply(Pointer pchRenderModelName, Pointer pchOriginalPath, int unOriginalPathLen, IntByReference peError);
	};
	/** <i>native declaration : headers\openvr_capi.h:2250</i> */
	public interface GetRenderModelErrorNameFromEnum_callback extends Callback {
		Pointer apply(int error);
	};
	public VR_IVRRenderModels_FnTable() {
		super();
	}
        @Override
	protected List<String> getFieldOrder() {
		return Arrays.asList("LoadRenderModel_Async", "FreeRenderModel", "LoadTexture_Async", "FreeTexture", "LoadTextureD3D11_Async", "LoadIntoTextureD3D11_Async", "FreeTextureD3D11", "GetRenderModelName", "GetRenderModelCount", "GetComponentCount", "GetComponentName", "GetComponentButtonMask", "GetComponentRenderModelName", "GetComponentStateForDevicePath", "GetComponentState", "RenderModelHasComponent", "GetRenderModelThumbnailURL", "GetRenderModelOriginalPath", "GetRenderModelErrorNameFromEnum");
	}
	public VR_IVRRenderModels_FnTable(Pointer peer) {
		super(peer);
	}
	public static class ByReference extends VR_IVRRenderModels_FnTable implements Structure.ByReference {
		
	};
	public static class ByValue extends VR_IVRRenderModels_FnTable implements Structure.ByValue {
		
	};
}

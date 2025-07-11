package games.rednblack.editor.utils.asset.impl;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import games.rednblack.editor.renderer.data.TexturePackVO;
import games.rednblack.editor.utils.runtime.TalosExportFormat;
import games.rednblack.h2d.common.H2DDialogs;
import games.rednblack.talos.runtime.ParticleEffectDescriptor;
import games.rednblack.talos.runtime.ParticleEmitterDescriptor;
import games.rednblack.talos.runtime.modules.*;
import games.rednblack.editor.proxy.ProjectManager;
import games.rednblack.editor.proxy.ResolutionManager;
import games.rednblack.editor.proxy.SceneDataManager;
import games.rednblack.editor.renderer.data.CompositeItemVO;
import games.rednblack.editor.renderer.data.MainItemVO;
import games.rednblack.editor.renderer.data.SceneVO;
import games.rednblack.h2d.common.vo.ExportMapperVO;
import games.rednblack.h2d.extension.talos.TalosComponent;
import games.rednblack.h2d.extension.talos.TalosVO;
import games.rednblack.editor.utils.AssetsUtils;
import games.rednblack.editor.utils.asset.Asset;
import games.rednblack.editor.utils.runtime.EntityUtils;
import games.rednblack.editor.utils.runtime.SandboxComponentRetriever;
import games.rednblack.editor.view.stage.Sandbox;
import games.rednblack.h2d.common.ProgressHandler;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

public class TalosVFXAsset extends Asset {

    Json talosJson = new Json();

    public TalosVFXAsset() {
        talosJson.setIgnoreUnknownFields(true);
        ParticleEmitterDescriptor.registerModules();
        for (Class clazz: ParticleEmitterDescriptor.registeredModules) {
            talosJson.addClassTag(clazz.getSimpleName(), TalosExportFormat.Module.class);
        }
    }

    @Override
    protected boolean matchMimeType(FileHandle file) {
        try {
            String contents = FileUtils.readFileToString(file.file(), "utf-8");
            return file.extension().equalsIgnoreCase("p") && contents.contains("emitters");
        } catch (Exception ignore) {
        }
        return false;
    }

    @Override
    public int getType() {
        return AssetsUtils.TYPE_TALOS_VFX;
    }

    @Override
    public boolean checkExistence(Array<FileHandle> files) {
        for (FileHandle file : new Array.ArrayIterator<>(files)) {
            FileHandle fileHandle = new FileHandle(projectManager.getCurrentProjectPath() + File.separator
                    + ProjectManager.TALOS_VFX_DIR_PATH + File.separator + file.nameWithoutExtension() + ".p");
            if (fileHandle.exists())
                return true;
        }
        return false;
    }

    @Override
    public void importAsset(Array<FileHandle> files, ProgressHandler progressHandler, boolean skipRepack) {
        final String targetPath = projectManager.getCurrentProjectPath() + File.separator + ProjectManager.TALOS_VFX_DIR_PATH;
        Array<FileHandle> images = new Array<>();
        Array<FileHandle> assetsRes = new Array<>();
        for (FileHandle fileHandle : new Array.ArrayIterator<>(files)) {
            if (!fileHandle.isDirectory() && fileHandle.exists()) {
                try {
                    TalosExportFormat talosResources = talosJson.fromJson(TalosExportFormat.class, fileHandle);
                    //copy images
                    boolean allImagesFound = addTalosImages(talosResources, fileHandle, images);
                    if (allImagesFound) {
                        boolean allAssetFound = addTalosRes(talosResources, fileHandle, assetsRes);
                        if (allAssetFound) {
                            // copy the fileHandle
                            String newName = fileHandle.name();
                            File target = new File(targetPath + "/" + newName);
                            FileUtils.copyFile(fileHandle.file(), target);
                        }
                    }
                } catch (IllegalArgumentException e) {
                    System.out.println("Error importing particles.");
                    throw e;
                } catch (IOException e) {
                    System.out.println("Error importing particles.");
                    e.printStackTrace();
                }
            }
        }
        if (images.size > 0) {
            projectManager.copyImageFilesForAllResolutionsIntoProject(images, false, progressHandler);

            for (FileHandle handle : new Array.ArrayIterator<>(images)) {
                String name = handle.nameWithoutExtension();
                boolean resDuplicated = false;
                for (TexturePackVO texturePackVO : projectManager.getCurrentProjectInfoVO().imagesPacks.values()) {
                    if (texturePackVO.regions.contains(name)) {
                        resDuplicated = true;
                        break;
                    }
                }
                if (!resDuplicated) {
                    projectManager.getCurrentProjectInfoVO().imagesPacks.get("main").regions.add(name);
                }
            }
        }
        if (assetsRes.size > 0) {
            for (FileHandle fileHandle : assetsRes) {
                try {
                    String newName = fileHandle.name();
                    File target = new File(targetPath + "/" + newName);
                    FileUtils.copyFile(fileHandle.file(), target);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (!skipRepack) {
            ResolutionManager resolutionManager = facade.retrieveProxy(ResolutionManager.NAME);
            resolutionManager.rePackProjectImagesForAllResolutionsSync();
        }
    }

    @Override
    public boolean deleteAsset(int root, String name) {
        String particlePath = projectManager.getCurrentProjectPath() + File.separator + ProjectManager.TALOS_VFX_DIR_PATH + File.separator;
        String filePath = particlePath + name;

        FileHandle fileHandle = new FileHandle(filePath);

        TalosExportFormat talosResources = talosJson.fromJson(TalosExportFormat.class, fileHandle);

        if (fileHandle.delete()) {
            deleteEntitiesWithParticleEffects(root, name); // delete entities from scene
            deleteAllItemsWithParticleName(name);

            Array<String> resources = talosResources.metadata.resources;
            for (String res : resources) {
                String resPath = particlePath + res;
                FileHandle resHandle = new FileHandle(resPath);
                resHandle.delete();
            }
            return true;
        }
        return false;
    }

    private boolean addTalosImages(TalosExportFormat talosResources, FileHandle fileHandle, Array<FileHandle> imgs) {
        try {
            Array<String> resources = talosResources.metadata.resources;
            for (String res : resources) {
                if (res.endsWith(".fga") || res.endsWith(".shdr"))
                    continue;

                res += ".png";
                File file = new File(FilenameUtils.getFullPath(fileHandle.path()) + res);
                if (file.exists()) {
                    imgs.add(new FileHandle(file));
                } else {
                    H2DDialogs.showErrorDialog(Sandbox.getInstance().getUIStage(),
                            "\nCould not find " + file.getName() + ".\nCheck if the file exists in the same directory.").padBottom(20).pack();
                    imgs.clear();
                    return false;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    private boolean addTalosRes(TalosExportFormat talosResources, FileHandle fileHandle, Array<FileHandle> imgs) {
        try {
            Array<String> resources = talosResources.metadata.resources;
            for (String res : resources) {
                if (res.endsWith(".fga") || res.endsWith(".shdr")) {
                    File file = new File(FilenameUtils.getFullPath(fileHandle.path()) + res);
                    if (file.exists()) {
                        imgs.add(new FileHandle(file));
                    } else {
                        H2DDialogs.showErrorDialog(Sandbox.getInstance().getUIStage(),
                                "\nCould not find " + file.getName() + ".\nCheck if the file exists in the same directory.").padBottom(20).pack();
                        imgs.clear();
                        return false;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    private void deleteAllItemsWithParticleName(String name) {
        for (CompositeItemVO compositeItemVO : projectManager.getCurrentProjectInfoVO().libraryItems.values()) {
            deleteAllParticles(compositeItemVO, name);
        }

        for (SceneVO scene : projectManager.currentProjectInfoVO.scenes) {
            SceneVO loadedScene = resourceManager.getSceneVO(scene.sceneName);
            CompositeItemVO tmpVo = new CompositeItemVO(loadedScene.composite);
            deleteAllParticles(tmpVo, name);
            loadedScene.composite = tmpVo;
            SceneDataManager sceneDataManager = facade.retrieveProxy(SceneDataManager.NAME);
            sceneDataManager.saveScene(loadedScene);
        }
    }

    private void deleteAllParticles(CompositeItemVO compositeItemVO, String name) {
        Consumer<CompositeItemVO> action = (rootItemVo) -> getParticles(rootItemVo, name);
        EntityUtils.applyActionRecursivelyOnLibraryItems(compositeItemVO, action);
    }

    private void getParticles(CompositeItemVO compositeItemVO, String name) {
        tmpImageList.clear();
        if (compositeItemVO != null && compositeItemVO.getElementsArray(TalosVO.class).size != 0) {
            Array<TalosVO> particleEffectList = compositeItemVO.getElementsArray(TalosVO.class);

            for (TalosVO spriteVO :particleEffectList)
                if (spriteVO.getResourceName().equals(name))
                    tmpImageList.add(spriteVO);

            particleEffectList.removeAll(tmpImageList, true);
        }
    }

    private void deleteEntitiesWithParticleEffects(int rootEntity, String particleName) {
        tmpEntityList.clear();
        Consumer<Integer> action = (root) -> {
            TalosComponent particleComponent = SandboxComponentRetriever.get(root, TalosComponent.class);
            if (particleComponent != null && particleComponent.particleName.equals(particleName)) {
                tmpEntityList.add(root);
            }
        };
        EntityUtils.applyActionRecursivelyOnEntities(rootEntity, action);
        EntityUtils.removeEntities(tmpEntityList);
    }

    @Override
    public boolean exportAsset(MainItemVO item, ExportMapperVO exportMapperVO, File tmpDir) throws IOException {
        super.exportAsset(item, exportMapperVO, tmpDir);
        TalosVO talosVO = (TalosVO) item;
        File fileSrc = new File(currentProjectPath + ProjectManager.TALOS_VFX_DIR_PATH + File.separator + talosVO.particleName);
        FileUtils.copyFileToDirectory(fileSrc, tmpDir);
        exportMapperVO.mapper.add(new ExportMapperVO.ExportedAsset(AssetsUtils.TYPE_TALOS_VFX, fileSrc.getName()));
        ParticleEffectDescriptor particleEffect = resourceManager.getProjectTalosList().get(talosVO.particleName).getParticleEffectDescriptor();
        for (ParticleEmitterDescriptor emitter : new Array.ArrayIterator<>(particleEffect.emitterModuleGraphs)) {
            for (AbstractModule module : new Array.ArrayIterator<>(emitter.getModules())) {
                if (module instanceof TextureModule) {
                    String path = ((TextureModule) module).regionName + ".png";
                    File f = new File(currentProjectPath + ProjectManager.IMAGE_DIR_PATH + File.separator + path);
                    FileUtils.copyFileToDirectory(f, tmpDir);
                }

                if (module instanceof PolylineModule) {
                    String path = ((PolylineModule) module).regionName + ".png";
                    File f = new File(currentProjectPath + ProjectManager.IMAGE_DIR_PATH + File.separator + path);
                    FileUtils.copyFileToDirectory(f, tmpDir);
                }

                if (module instanceof FlipbookModule) {
                    String path = ((FlipbookModule) module).regionName + ".png";
                    File f = new File(currentProjectPath + ProjectManager.IMAGE_DIR_PATH + File.separator + path);
                    FileUtils.copyFileToDirectory(f, tmpDir);
                }

                if (module instanceof ShadedSpriteModule) {
                    String path = ((ShadedSpriteModule) module).shdrFileName;
                    File f = new File(currentProjectPath + ProjectManager.TALOS_VFX_DIR_PATH + File.separator + path);
                    FileUtils.copyFileToDirectory(f, tmpDir);
                }

                if (module instanceof VectorFieldModule) {
                    String path = ((VectorFieldModule) module).fgaFileName;
                    File f = new File(currentProjectPath + ProjectManager.TALOS_VFX_DIR_PATH + File.separator + path);
                    FileUtils.copyFileToDirectory(f, tmpDir);
                }
            }
        }
        return true;
    }
}

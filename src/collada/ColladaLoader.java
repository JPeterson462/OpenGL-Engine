package collada;

import java.io.InputStream;

import collada.data.AnimatedModelData;
import collada.data.AnimationData;
import collada.data.MeshData;
import collada.data.SkeletonData;
import collada.data.SkinningData;
import utils.XMLNode;
import utils.XMLParser;

public class ColladaLoader {

	public static AnimatedModelData loadColladaModel(InputStream colladaFile, int maxWeights, String animation) {
		XMLNode node = XMLParser.loadXmlFile(colladaFile);

		SkinLoader skinLoader = new SkinLoader(node.getChild("library_controllers"), maxWeights);
		SkinningData skinningData = skinLoader.extractSkinData();

		SkeletonLoader jointsLoader = new SkeletonLoader(node.getChild("library_visual_scenes"), skinningData.jointOrder, animation);
		SkeletonData jointsData = jointsLoader.extractBoneData();

		GeometryLoader g = new GeometryLoader(node.getChild("library_geometries"), skinningData.verticesSkinData);
		MeshData meshData = g.extractModelData();

		return new AnimatedModelData(meshData, jointsData);
	}

	public static AnimationData loadColladaAnimation(InputStream colladaFile, String animation) {
		XMLNode node = XMLParser.loadXmlFile(colladaFile);
		XMLNode animNode = node.getChild("library_animations");
		XMLNode jointsNode = node.getChild("library_visual_scenes");
		AnimationLoader loader = new AnimationLoader(animNode, jointsNode, animation);
		AnimationData animData = loader.extractAnimation();
		return animData;
	}

}

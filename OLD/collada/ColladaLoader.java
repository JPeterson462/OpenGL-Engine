package collada;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import collada.animation.AnimatedModelData;
import collada.animation.AnimationData;
import collada.animation.MeshData;
import collada.animation.SkeletonData;
import collada.animation.SkinningData;

public class ColladaLoader {

	public static AnimatedModelData loadColladaModel(InputStream colladaFile, int maxWeights) throws SAXException, IOException, ParserConfigurationException {
		XMLNode node = XMLParser.loadXML(colladaFile);

		SkinLoader skinLoader = new SkinLoader(node.getChild("library_controllers"), maxWeights);
		SkinningData skinningData = skinLoader.extractSkinData();

		SkeletonLoader jointsLoader = new SkeletonLoader(node.getChild("library_visual_scenes"), skinningData.jointOrder);
		SkeletonData jointsData = jointsLoader.extractBoneData();

		GeometryLoader g = new GeometryLoader(node.getChild("library_geometries"), skinningData.verticesSkinData);
		MeshData meshData = g.extractModelData();

		return new AnimatedModelData(meshData, jointsData);
	}

	public static AnimationData loadColladaAnimation(InputStream colladaFile) throws SAXException, IOException, ParserConfigurationException {
		XMLNode node = XMLParser.loadXML(colladaFile);
		XMLNode animNode = node.getChild("library_animations");
		XMLNode jointsNode = node.getChild("library_visual_scenes");
		AnimationLoader loader = new AnimationLoader(animNode, jointsNode);
		AnimationData animData = loader.extractAnimation();
		return animData;
	}

}

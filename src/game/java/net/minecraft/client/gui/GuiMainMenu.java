package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import net.lax1dude.eaglercraft.EagRuntime;
import net.lax1dude.eaglercraft.EaglercraftRandom;
import net.lax1dude.eaglercraft.EaglercraftVersion;
import net.lax1dude.eaglercraft.IOUtils;
import net.lax1dude.eaglercraft.internal.PlatformApplication;
import net.lax1dude.eaglercraft.internal.PlatformOpenGL;
import net.lax1dude.eaglercraft.minecraft.MainMenuSkyboxTexture;
import net.lax1dude.eaglercraft.opengl.WorldRenderer;
import net.lax1dude.eaglercraft.sp.SingleplayerServerController;
import net.lax1dude.eaglercraft.sp.gui.GuiScreenIntegratedServerBusy;
import net.lax1dude.eaglercraft.sp.gui.GuiScreenIntegratedServerStartup;
import net.lax1dude.eaglercraft.opengl.EaglercraftGPU;
import net.lax1dude.eaglercraft.opengl.GlStateManager;
import net.lax1dude.eaglercraft.opengl.RealOpenGLEnums;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.IResource;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.WorldServerDemo;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.WorldInfo;
import net.peyton.eagler.gui.GuiCredits;
import net.lax1dude.eaglercraft.profile.*;

public class GuiMainMenu extends GuiScreen {
	private static final EaglercraftRandom RANDOM = new EaglercraftRandom();
	private final float updateCounter;
	private String splashText;
	private GuiButton buttonResetDemo;
	private float panoramaTimer;

	private static MainMenuSkyboxTexture viewportTexture = null;
	private static MainMenuSkyboxTexture viewportTexture2 = null;

	public static final String MORE_INFO_TEXT = "Please click " + TextFormatting.UNDERLINE + "here" + TextFormatting.RESET + " for more information.";
	private static final ResourceLocation SPLASH_TEXTS = new ResourceLocation("texts/splashes.txt");

	private static final ResourceLocation[] TITLE_PANORAMA_PATHS = new ResourceLocation[] {
			new ResourceLocation("textures/gui/title/background/panorama_0.png"),
			new ResourceLocation("textures/gui/title/background/panorama_1.png"),
			new ResourceLocation("textures/gui/title/background/panorama_2.png"),
			new ResourceLocation("textures/gui/title/background/panorama_3.png"),
			new ResourceLocation("textures/gui/title/background/panorama_4.png"),
			new ResourceLocation("textures/gui/title/background/panorama_5.png") };
	
	private ResourceLocation backgroundTexture = null;
	private static ResourceLocation backgroundTexture2 = null;
	private int field_193978_M;
	private int field_193979_N;

	public GuiMainMenu() {
		this.splashText = "missingno";
		IResource iresource = null;
		try {
			List<String> list = Lists.<String>newArrayList();
			iresource = Minecraft.getMinecraft().getResourceManager().getResource(SPLASH_TEXTS);
			BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(iresource.getInputStream(), StandardCharsets.UTF_8));
			String s;
			while ((s = bufferedreader.readLine()) != null) {
				s = s.trim();
				if (!s.isEmpty()) {
					list.add(s);
				}
			}
			if (!list.isEmpty()) {
				while (true) {
					this.splashText = list.get(RANDOM.nextInt(list.size()));
					if (this.splashText.hashCode() != 125780783) {
						break;
					}
				}
			}
		} catch (IOException var8) {
			;
		} finally {
			IOUtils.closeQuietly((Closeable) iresource);
		}
		this.updateCounter = RANDOM.nextFloat();
	}

	public void updateScreen() {
		this.panoramaTimer += 1.0F;
	}

	public boolean doesGuiPauseGame() {
		return false;
	}

	protected void keyTyped(char typedChar, int keyCode) throws IOException {
	}

	public void initGui() {
		if (!this.mc.gameSettings.hasSeenFirstLoad) {
			this.mc.displayGuiScreen(new GuiScreenFirstLoad(this.mc.gameSettings));
			return;
		}
		viewportTexture = new MainMenuSkyboxTexture(256, 256);
		this.backgroundTexture = this.mc.getTextureManager().getDynamicTextureLocation("background", this.viewportTexture);
		viewportTexture2 = new MainMenuSkyboxTexture(256, 256);
		backgroundTexture2 = this.mc.getTextureManager().getDynamicTextureLocation("background", viewportTexture2);
		
		this.field_193978_M = this.fontRendererObj.getStringWidth("Resources copyright Mojang AB");
		this.field_193979_N = this.width - this.field_193978_M - 2;
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		if (calendar.get(2) + 1 == 12 && calendar.get(5) == 24) {
			this.splashText = I18n.format("eaglercraft.splashes.xmas");
		} else if (calendar.get(2) + 1 == 1 && calendar.get(5) == 1) {
			this.splashText = I18n.format("eaglercraft.splashes.newyear");
		} else if (calendar.get(2) + 1 == 10 && calendar.get(5) == 31) {
			this.splashText = I18n.format("eaglercraft.splashes.halloween");
		}

		int startY = this.height / 2 - 30;
		if (this.mc.isDemo()) {
			this.addDemoButtons(startY, 22);
		} else {
			this.addSingleplayerMultiplayerButtons(startY, 22);
		}

		this.buttonList.add(new GuiButton(0, this.width / 2 - 100, startY + 66, 98, 20, I18n.format("menu.options")));
		this.buttonList.add(new GuiButton(4, this.width / 2 + 2, startY + 66, 98, 20, I18n.format("Edit Profile")));
		this.buttonList.add(new GuiButtonLanguage(5, this.width / 2 - 124, startY + 66));
	}

	private void addSingleplayerMultiplayerButtons(int startY, int spacing) {
		this.buttonList.add(new GuiButton(1, this.width / 2 - 100, startY, I18n.format("menu.singleplayer")));
		this.buttonList.add(new GuiButton(2, this.width / 2 - 100, startY + spacing * 1, I18n.format("menu.multiplayer")));
		this.buttonList.add(new GuiButton(14, this.width / 2 - 100, startY + spacing * 2, I18n.format("menu.credits")));
	}

	private void addDemoButtons(int startY, int spacing) {
		this.buttonList.add(new GuiButton(11, this.width / 2 - 100, startY, I18n.format("menu.playdemo")));
		this.buttonResetDemo = this.addButton(new GuiButton(12, this.width / 2 - 100, startY + spacing * 1, I18n.format("menu.resetdemo")));
	}
	protected void actionPerformed(GuiButton button) throws IOException {
		if (button.id == 0) {
			this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
		}
		if (button.id == 5) {
			this.mc.displayGuiScreen(new GuiLanguage(this, this.mc.gameSettings, this.mc.getLanguageManager()));
		}
		if (button.id == 1) {
			if (this.mc.isDemo()) {
				return;
			}
			this.mc.displayGuiScreen(new GuiScreenIntegratedServerStartup(this));
		}
		if (button.id == 2) {
			this.mc.displayGuiScreen(new GuiMultiplayer(this));
		}
		if (button.id == 14) {
			this.mc.displayGuiScreen(new GuiCredits(this, ""));
		}
		if (button.id == 4) {
			this.mc.displayGuiScreen(new GuiScreenEditProfile(this));
		}
		if (button.id == 11 || button.id == 12) {
			// Demo mode handlers mapped safe
		}
	}

	public void confirmClicked(boolean result, int id) {
		this.mc.displayGuiScreen(this);
	}

	private void drawPanorama(int mouseX, int mouseY, float partialTicks) {
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getBuffer();
		GlStateManager.matrixMode(RealOpenGLEnums.GL_PROJECTION);
		GlStateManager.pushMatrix();
		GlStateManager.loadIdentity();
		
		float fovy = 120.0F;
		float aspect = 1.0F;
		float zNear = 0.05F;
		float zFar = 10.0F;
		float cotangent = 1.0F / (float) Math.tan(fovy * 3.141592653589793D / 360.0D);
		GlStateManager.multMatrix(new float[] {
			cotangent / aspect, 0.0F, 0.0F, 0.0F,
			0.0F, cotangent, 0.0F, 0.0F,
			0.0F, 0.0F, (zFar + zNear) / (zNear - zFar), -1.0F,
			0.0F, 0.0F, (2.0F * zFar * zNear) / (zNear - zFar), 0.0F
		});

		GlStateManager.matrixMode(RealOpenGLEnums.GL_MODELVIEW);
		GlStateManager.pushMatrix();
		GlStateManager.loadIdentity();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotate(MathHelper.sin((this.panoramaTimer + partialTicks) / 400.0F) * 25.0F + 20.0F, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotate(-(this.panoramaTimer + partialTicks) * 0.1F, 0.0F, 1.0F, 0.0F);

		for (int i = 0; i < 6; ++i) {
			GlStateManager.pushMatrix();
			if (i == 1) GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
			if (i == 2) GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
			if (i == 3) GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
			if (i == 4) GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
			if (i == 5) GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);

			this.mc.getTextureManager().bindTexture(TITLE_PANORAMA_PATHS[i]);
			worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
			int j = 255;
			worldrenderer.pos(-1.0D, -1.0D, -1.0D).tex(0.0D, 0.0D).color(255, 255, 255, j).endVertex();
			worldrenderer.pos(1.0D, -1.0D, -1.0D).tex(1.0D, 0.0D).color(255, 255, 255, j).endVertex();
			worldrenderer.pos(1.0D, 1.0D, -1.0D).tex(1.0D, 1.0D).color(255, 255, 255, j).endVertex();
			worldrenderer.pos(-1.0D, 1.0D, -1.0D).tex(0.0D, 1.0D).color(255, 255, 255, j).endVertex();
			tessellator.draw();
			GlStateManager.popMatrix();
		}
		GlStateManager.popMatrix();
		GlStateManager.matrixMode(RealOpenGLEnums.GL_PROJECTION);
		GlStateManager.popMatrix();
		GlStateManager.matrixMode(RealOpenGLEnums.GL_MODELVIEW);
	}

	private void rotateAndBlurSkybox(float partialTicks) {
		this.mc.getTextureManager().bindTexture(this.backgroundTexture);
		
		GlStateManager.enableBlend();
		GlStateManager.colorMask(true, true, true, false);
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getBuffer();
		worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
		GlStateManager.disableAlpha();

		for (int i = 0; i < 3; ++i) {
			float f = 1.0F / (float)(i + 1);
			int j = this.width;
			int k = this.height;
			float f1 = (float)(i - 1) / 256.0F;
			worldrenderer.pos((double)j, (double)k, (double)this.zLevel).tex((double)(0.0F + f1), 1.0D).color(1.0F, 1.0F, 1.0F, f).endVertex();
			worldrenderer.pos((double)j, 0.0D, (double)this.zLevel).tex((double)(1.0F + f1), 1.0D).color(1.0F, 1.0F, 1.0F, f).endVertex();
			worldrenderer.pos(0.0D, 0.0D, (double)this.zLevel).tex((double)(1.0F + f1), 0.0D).color(1.0F, 1.0F, 1.0F, f).endVertex();
			worldrenderer.pos(0.0D, (double)k, (double)this.zLevel).tex((double)(0.0F + f1), 0.0D).color(1.0F, 1.0F, 1.0F, f).endVertex();
		}
		tessellator.draw();
		GlStateManager.enableAlpha();
		GlStateManager.colorMask(true, true, true, true);
	}

	private void renderSkybox(int mouseX, int mouseY, float partialTicks) {
		this.drawPanorama(mouseX, mouseY, partialTicks);
		this.rotateAndBlurSkybox(partialTicks);
		this.rotateAndBlurSkybox(partialTicks);
		this.rotateAndBlurSkybox(partialTicks);
		this.rotateAndBlurSkybox(partialTicks);
		this.rotateAndBlurSkybox(partialTicks);
		this.rotateAndBlurSkybox(partialTicks);
		this.rotateAndBlurSkybox(partialTicks);
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getBuffer();
		worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
		float f = (float)(this.width > this.height ? 120.0D / (double)this.width : 120.0D / (double)this.height);
		float f1 = (float)this.height * f / 256.0F;
		float f2 = (float)this.width * f / 256.0F;
		int i = this.width;
		int j = this.height;
		worldrenderer.pos((double)i, (double)j, (double)this.zLevel).tex((double)(0.5F - f1), (double)(0.5F + f2)).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
		worldrenderer.pos((double)i, 0.0D, (double)this.zLevel).tex((double)(0.5F - f1), (double)(0.5F - f2)).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
		worldrenderer.pos(0.0D, 0.0D, (double)this.zLevel).tex((double)(0.5F + f1), (double)(0.5F - f2)).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
		worldrenderer.pos(0.0D, (double)j, (double)this.zLevel).tex((double)(0.5F + f1), (double)(0.5F + f2)).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
		tessellator.draw();
	}

	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.renderSkybox(mouseX, mouseY, partialTicks);

		int panelWidth = 240;
		int panelHeight = 180;
		int panelLeft = this.width / 2 - (panelWidth / 2);
		int panelTop = this.height / 2 - 80;
		
		this.drawGradientRect(panelLeft, panelTop, panelLeft + panelWidth, panelTop + panelHeight, 0xCE0A0D14, 0xCE0A0D14);
		this.drawHorizontalLine(panelLeft, panelLeft + panelWidth, panelTop, 0x3FFFFFFF);
		this.drawHorizontalLine(panelLeft, panelLeft + panelWidth, panelTop + panelHeight, 0x3FFFFFFF);
		this.drawVerticalLine(panelLeft, panelTop, panelTop + panelHeight, 0x3FFFFFFF);
		this.drawVerticalLine(panelLeft + panelWidth, panelTop, panelTop + panelHeight, 0x3FFFFFFF);

		GlStateManager.pushMatrix();
		GlStateManager.translate((float)(this.width / 2), (float)(panelTop + 16), 0.0F);
		GlStateManager.scale(2.5F, 2.5F, 2.5F);
		
		String titleText = "NoneClient";
		int textWidth = this.fontRendererObj.getStringWidth(titleText);
		this.fontRendererObj.drawStringWithShadow(TextFormatting.BOLD + titleText, (float)(-textWidth / 2), 0.0F, 0xFFFFFF);
		GlStateManager.popMatrix();

		String versionTag = TextFormatting.GRAY + "v1.12.2";
		this.fontRendererObj.drawStringWithShadow(versionTag, 2.0F, (float)(this.height - 10), 16777215);

		String copyrightText = "Resources copyright Mojang AB";
		this.fontRendererObj.drawStringWithShadow(copyrightText, (float)(this.width - this.fontRendererObj.getStringWidth(copyrightText) - 2), (float)(this.height - 10), 16777215);

		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
	}
}

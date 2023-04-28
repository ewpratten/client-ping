package com.ewpratten.client_ping;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import net.minecraft.client.MinecraftClient;

public class Native {

	/**
	 * Internal utility for loading native libraries from the jar file
	 */
	private class NativeLibraryLoader {

		/**
		 * Operating systems
		 */
		protected enum OperatingSystem {
			Windows,
			Linux,
			MacOs;

			/**
			 * Get the current host's operating system
			 *
			 * @return Operating System
			 */
			protected static OperatingSystem getCurrent() {
				String os = System.getProperty("os.name").toLowerCase();

				if (os.contains("win")) {
					return Windows;
				} else if (os.contains("mac")) {
					return MacOs;
				} else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
					return Linux;
				} else {
					throw new RuntimeException("Unsupported operating system: " + os);
				}
			}
		}

		/**
		 * Hardware architectures
		 */
		protected enum Architecture {
			x86,
			x86_64,
			aarch64;

			/**
			 * Get the current host's architecture
			 *
			 * @return Architecture
			 */
			protected static Architecture getCurrent() {
				String arch = System.getProperty("os.arch").toLowerCase();

				switch (arch) {
					case "x86":
					case "i386":
					case "i486":
					case "i586":
					case "i686":
						return x86;
					case "x86_64":
					case "amd64":
						return x86_64;
					case "aarch64":
						return aarch64;
					default:
						throw new RuntimeException("Unsupported architecture: " + arch);
				}
			}
		}

		/**
		 * A host platform
		 */
		protected record Platform(OperatingSystem os, Architecture arch) {
			/**
			 * Get the current host's platform
			 *
			 * @return Host platform
			 */
			public static Platform getCurrent() {
				return new Platform(OperatingSystem.getCurrent(), Architecture.getCurrent());
			}
		}

		/**
		 * Determine the correct file name for a library
		 *
		 * @param platform Host platform
		 * @param libName  Library name
		 * @return File name
		 */
		private static String determineLibraryFileName(Platform platform, String libName) {
			switch (platform.os()) {
				case Windows:
					return libName + ".dll";
				case Linux:
					return "lib" + libName + ".so";
				case MacOs:
					return "lib" + libName + ".dylib";
				default:
					throw new RuntimeException("Unsupported operating system: " + platform.os());
			}
		}

		private static String getLibraryDirOnDiskInDevelopment(Platform platform) {
			return String.format("../build/resources/main/native/%s/%s",
					platform.os().name().toLowerCase(),
					platform.arch().name().toLowerCase());
		}

		/**
		 * Get the directory in the jar where the libraries are stored
		 *
		 * @param platform Host platform
		 * @return Library directory
		 */
		private static String getLibraryDirInJar(Platform platform) {
			return String.format("native/%s/%s", platform.os().name().toLowerCase(),
					platform.arch().name().toLowerCase());
		}

		/**
		 * Construct the path to a library file in the current jar
		 *
		 * @param platform Host platform
		 * @param libName  Library name
		 * @return Library file path
		 */
		private static String getLibraryPathInJar(Platform platform, String libName) {
			return String.format("%s/%s", getLibraryDirInJar(platform),
					determineLibraryFileName(platform, libName));
		}

		protected static void loadLibrary(Platform platform, String libName) throws IOException {

			// Get the library path in the jar
			String libraryPathInJar = getLibraryPathInJar(platform, libName);
			String libraryPathOnDiskDev = getLibraryDirOnDiskInDevelopment(platform) + "/"
					+ determineLibraryFileName(platform, libName);

			// Check if we are to be running in development mode
			String currentDirectory = System.getProperty("user.dir");
			boolean devMode = currentDirectory.endsWith("client-ping/run");

			// Construct the extraction destination path
			MinecraftClient mc = MinecraftClient.getInstance();
			String extractionPath = String.format("%s/mods/%s", mc.runDirectory.getAbsolutePath(),
					libraryPathInJar);

			// Ensure that the extraction destination directory exists
			Globals.LOGGER.info("Native library shall be extracted to: " + extractionPath);
			Files.createDirectories(Path.of(extractionPath).getParent());

			// If the extraction destination file already exists, delete it
			if (Files.exists(Path.of(extractionPath))) {
				Globals.LOGGER.info("Replacing existing native library");
				Files.delete(Path.of(extractionPath));
			}

			// Extract the library from the jar
			InputStream libStream = null;
			if (devMode) {
				// We are in development mode, so the library is on disk
				Globals.LOGGER.warn("Using on-disk copy of native library because we are in development mode.");
				libStream = new FileInputStream(libraryPathOnDiskDev);
			} else {
				libStream = Native.class.getClassLoader().getResourceAsStream("/" + libraryPathInJar);
				if (libStream == null) {
					Globals.LOGGER.error("Failed to load native library from jar. Is it missing?");
					throw new RuntimeException("Failed to load native library from jar.");
				}
			}
			Files.copy(libStream, Path.of(extractionPath));

			// Load the library
			Globals.LOGGER.info("Loading native library from: " + extractionPath);
			System.load(extractionPath);

		}

	}

	// All platforms supported by this mod
	private static final NativeLibraryLoader.Platform[] SUPPORTED_PLATFORMS = {
			new NativeLibraryLoader.Platform(NativeLibraryLoader.OperatingSystem.Windows,
					NativeLibraryLoader.Architecture.x86_64),
			new NativeLibraryLoader.Platform(NativeLibraryLoader.OperatingSystem.Linux,
					NativeLibraryLoader.Architecture.x86_64),
	};

	// Load the native library
	static {

		// Determine the current platform
		NativeLibraryLoader.Platform currentPlatform = NativeLibraryLoader.Platform.getCurrent();

		// If the current platform is not supported, complain
		if (!List.of(SUPPORTED_PLATFORMS).contains(currentPlatform)) {
			Globals.LOGGER.error("Unsupported platform: " + currentPlatform);
			throw new RuntimeException("Unsupported platform: " + currentPlatform);
		}

		// Load the native library
		try {
			NativeLibraryLoader.loadLibrary(currentPlatform, "client_ping_native");
		} catch (IOException e) {
			Globals.LOGGER.error("Failed to load native library");
			e.printStackTrace();
			throw new RuntimeException("Failed to load native library");
		}

	}

	public static native String helloWorld();

}

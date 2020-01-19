package main

import (
	"fmt"
	"os"
	"path"
	"path/filepath"
	"strings"
)

func main() {
	argsWithoutProg := os.Args[1:]

	if len(argsWithoutProg) != 2 {
		fmt.Println("Usage: VLC_ANDROID_DIR NEW_APP_ID")
		fmt.Println("Example: /Users/user/vlc-android com.example.app")
		return
	}

	// args
	vlcAndroidDir := argsWithoutProg[0]
	newAppId := argsWithoutProg[1]

	// define constants
	parts := strings.Split(newAppId, ".")
	firstParts := parts[:len(parts)-1]
	baseFolderFirst := path.Join(firstParts...)
	baseFolderLast := parts[len(parts)-1]

	// get folders with org/videolan/vlc in path
	var sourcePaths []string
	filepath.Walk(vlcAndroidDir, func(itemPath string, info os.FileInfo, err error) error {
		if info.IsDir() == true {
			if info.Name() == "vlc" && strings.HasSuffix(itemPath, "org/videolan/vlc") == true {
				sourcePaths = append(sourcePaths, itemPath)
			}
		}
		return nil
	})

	// mv sourcePaths -> targetPaths corresponding to new applicationId
	for _, sourcePath := range sourcePaths {
		rootPath := strings.TrimSuffix(sourcePath, "org/videolan/vlc")
		targetBasePath := path.Join(rootPath, baseFolderFirst)
		targetPath := path.Join(targetBasePath, baseFolderLast)

		fmt.Println("########")
		fmt.Println(sourcePath)
		fmt.Println("-> " + targetPath)

		// mkdir targetBasePath
		os.MkdirAll(targetBasePath, os.ModePerm)

		// rename sourcePath -> targetPath
		os.Rename(sourcePath, targetPath)
	}
}

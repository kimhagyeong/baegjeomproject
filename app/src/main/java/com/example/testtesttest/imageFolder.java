package com.example.testtesttest;

public class imageFolder {
        private  String path;
        private  String FolderName;
        private int numberOfPics = 0;
        private String firstPic;

        public imageFolder(){
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getFolderName() {
            return FolderName;
        }

        public void setFolderName(String folderName) {
            FolderName = folderName;
        }

        public int getNumberOfPics() {
            return numberOfPics;
        }

        public void addPics(){
            this.numberOfPics++;
        }

        public String getFirstPic() {
            return firstPic;
        }

        public void setFirstPic(String firstPic) {
            this.firstPic = firstPic;
        }
}

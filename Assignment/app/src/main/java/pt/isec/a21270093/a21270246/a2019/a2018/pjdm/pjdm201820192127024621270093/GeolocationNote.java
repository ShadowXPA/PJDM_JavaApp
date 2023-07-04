package pt.isec.a21270093.a21270246.a2019.a2018.pjdm.pjdm201820192127024621270093;

import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GeolocationNote extends Note {
    private int NextID;
    private String FolderUrl;

    private List<String> ImgUrl;
    private double Lat;
    private double Lng;

    public GeolocationNote(int ID, String title, User author, Date creationDate, boolean shared, List<Integer> users, String folderUrl, double lat, double lng) {
        super(ID, title, author, creationDate, shared, users);
        FolderUrl = folderUrl;
        Lat = lat;
        Lng = lng;
        NextID = 0;
        ImgUrl = new ArrayList<>();
    }

    public void AddImg(File src) throws IOException {
        String str = FolderUrl.substring(FolderUrl.indexOf("/note_"));
        File prevPrevFolder = new File(FolderUrl.substring(0, FolderUrl.indexOf("/" + super.getAuthor() + str)));
        if (!prevPrevFolder.exists())
            prevPrevFolder.mkdir();
        File prevFolder = new File(FolderUrl.substring(0, FolderUrl.indexOf("/note_")));
        if (!prevFolder.exists())
            prevFolder.mkdir();
        File folder = new File(FolderUrl);
        if (!folder.exists())
            folder.mkdir();
        String dst = FolderUrl + "/img_" + NextID + ".jpg";
        File dsti = new File(dst);
        PNUtil.copy(src, dsti);
        if (!src.getAbsolutePath().equals(dsti.getAbsolutePath()))
            src.delete();
        if (!this.ImgUrl.contains(dst)) {
            this.ImgUrl.add(dst);
            NextID++;
        }
    }

    public void ClearImg() {
        for (int i = (this.ImgUrl.size() - 1); i >= 0; i--) {
            RemoveImg(i);
        }
        File folder = new File(this.FolderUrl);
        if (folder.exists())
            folder.delete();
    }

    public void RemoveImg(int index) {
        String dst = this.ImgUrl.get(index);
        File img = new File(dst);

        if (img.exists()) {
            if (img.delete())
                this.ImgUrl.remove(index);
        }
    }

    public void RemoveImg(String value) {
        if (this.ImgUrl.contains(value))
            RemoveImg(this.ImgUrl.indexOf(value));
    }

    public void setImgUrl(List<String> imgUrl) {
        this.ImgUrl = new ArrayList<>(imgUrl);
    }

    public List<String> getImgUrl() {
        return new ArrayList<>(ImgUrl);
    }

    public double getLat() {
        return Lat;
    }

    public double getLng() {
        return Lng;
    }

    public String getFolderUrl() {
        return FolderUrl;
    }

    public LatLng getLatLng() {
        return new LatLng(Lat, Lng);
    }

    @Override
    public String compareType() {
        return NoteType.GEOLOCATION_NOTE;
    }

}

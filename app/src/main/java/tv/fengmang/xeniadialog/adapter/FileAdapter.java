package tv.fengmang.xeniadialog.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import tv.fengmang.xeniadialog.R;


/**
 * User: vio_wang
 * Date: 2017-12-29
 * Description:
 */
public class FileAdapter extends RecyclerView.Adapter<FileAdapter.VH> {

    private static final String TAG = FileAdapter.class.getSimpleName();
    private DateFormat timeFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);
    private final static float GB_SIZE = 1024 * 1024 * 1024F;
    private final static float MB_SIZE = 1024 * 1024F;
    private final static float KB_SIZE = 1024F;
    private Context mContext;
    private File[] fileList;
    private List<File> selectFileList;  //记录选中的文件

    private int status = STATUS_NORMAL;
    public final static int STATUS_NORMAL = 0;
    public final static int STATUS_SELECT = 1;

    public FileAdapter(Context context, File[] fileList) {
        this.mContext = context;
        this.fileList = fileList;
        selectFileList = new ArrayList<>();
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.orange_item_file, parent, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        File file = fileList[position];
        holder.tvFileName.setText(file.getName());
        holder.tvFileDate.setText(formatDate(file.lastModified()));
        if (file.isDirectory()) {
            holder.tvFileType.setText("D");
            holder.tvFileInfo.setText(String.format("子文件个数:%d", file.list() != null ? file.list().length : 0));
        } else if (file.isFile()) {
            holder.tvFileType.setText("F");
            holder.tvFileInfo.setText(String.format("大小:%d 字节", file.length()));
        } else {
            holder.tvFileType.setText("N");
        }
        if (status == STATUS_NORMAL) {
            holder.cbFile.setVisibility(View.GONE);
        } else {
            holder.cbFile.setVisibility(View.VISIBLE);
            if (fileIsSelected(position)) {
                holder.cbFile.setChecked(true);
            } else {
                holder.cbFile.setChecked(false);
            }
        }
    }

    private String formatDate(long tes) {
        return timeFormatter.format(tes);
    }

    @Override
    public int getItemCount() {
        return fileList == null ? 0 : fileList.length;
    }


    public void clickPosition(int position) {
        if (status == STATUS_NORMAL) {
            return;
        }
        if (fileIsSelected(position)) {
            removeSelectFile(fileList[position]);
        } else {
            addSelectFile(fileList[position]);
        }
        notifyItemRangeChanged(position, 1);
    }

    public boolean fileIsSelected(int position) {
        if (selectFileList.isEmpty()) {
            return false;
        }
        File file = fileList[position];
        for (File selectFile : selectFileList) {
            if (file.getName().equalsIgnoreCase(selectFile.getName())) {
                return true;
            }
        }
        return false;
    }

    public void longClickPosition(int position) {
        if (status == STATUS_NORMAL) {
            status = STATUS_SELECT;
            addSelectFile(fileList[position]);
            notifyItemRangeChanged(0, getItemCount());
        }
    }

    public boolean isNormalStatus() {
        return status == STATUS_NORMAL;
    }

    private void addSelectFile(File file) {
        if (selectFileList.isEmpty()) {
            selectFileList.add(file);
        } else {
            boolean exist = false;
            for (File f : selectFileList) {
                if (f.getName().equalsIgnoreCase(file.getName())) {
                    exist = true;
                    break;
                }
            }
            if (!exist) {
                selectFileList.add(file);
            }
        }
    }

    private void removeSelectFile(File file) {
        if (selectFileList.isEmpty()) {
            return;
        }

        File tempFile = null;
        for (File f : selectFileList) {
            if (f.getName().equalsIgnoreCase(file.getName())) {
                tempFile = f;
                break;
            }
        }
        if (tempFile != null) {
            selectFileList.remove(tempFile);
        }
    }

    public void resetStatus() {
        if (status == STATUS_NORMAL) {
            return;
        }
        status = STATUS_NORMAL;
        selectFileList.clear();
        notifyItemRangeChanged(0, getItemCount());
    }


    static class VH extends RecyclerView.ViewHolder {
        private TextView tvFileName;
        private TextView tvFileDate;
        private TextView tvFileType;
        private TextView tvFileInfo;
        private CheckBox cbFile;


        public VH(View view) {
            super(view);
            tvFileName = itemView.findViewById(R.id.tv_file_name);
            tvFileDate = itemView.findViewById(R.id.tv_file_date);
            tvFileType = itemView.findViewById(R.id.tv_file_type);
            tvFileInfo = itemView.findViewById(R.id.tv_file_info);
            cbFile = itemView.findViewById(R.id.cb_file);
        }
    }
}

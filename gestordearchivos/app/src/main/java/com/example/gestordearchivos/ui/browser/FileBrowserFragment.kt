package com.example.gestordearchivos.ui.browser

import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gestordearchivos.R
import com.example.gestordearchivos.databinding.FragmentFileBrowserBinding
import java.io.File

class FileBrowserFragment : Fragment() {

    private var _binding: FragmentFileBrowserBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FileBrowserViewModel by viewModels()
    private lateinit var fileAdapter: FileAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFileBrowserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()

        if (savedInstanceState == null) {
            val rootPath = Environment.getExternalStorageDirectory().absolutePath
            viewModel.loadFiles(rootPath)
        }
    }

    private fun setupRecyclerView() {
        fileAdapter = FileAdapter { fileItem ->
            if (fileItem.isDirectory) {
                viewModel.loadFiles(fileItem.path)
            } else {
                openFile(fileItem.path)
            }
        }
        binding.rvFiles.apply {
            adapter = fileAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun observeViewModel() {
        viewModel.files.observe(viewLifecycleOwner) { fileList ->
            fileAdapter.submitList(fileList)
            binding.emptyView.isVisible = fileList.isEmpty()
        }

        viewModel.currentPath.observe(viewLifecycleOwner) { path ->
            // Aquí actualizarías los breadcrumbs
            (activity as? AppCompatActivity)?.supportActionBar?.subtitle = path
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.isVisible = isLoading
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMsg ->
            errorMsg?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                viewModel.onErrorShown()
            }
        }
    }

    private fun openFile(filePath: String) {
        // Lógica para abrir archivos con Intent y FileProvider
        try {
            val file = File(filePath)
            val uri = FileProvider.getUriForFile(requireContext(), "${requireContext().packageName}.provider", file)
            val mime = requireContext().contentResolver.getType(uri)

            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(uri, mime)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "No se puede abrir el archivo.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
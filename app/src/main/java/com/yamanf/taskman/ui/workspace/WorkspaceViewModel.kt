package com.yamanf.taskman.ui.workspace



import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.toObject
import com.yamanf.taskman.data.TaskModel
import com.yamanf.taskman.data.WorkspaceModel
import com.yamanf.taskman.firebase.FirebaseRepository
import com.yamanf.taskman.utils.Constants
import com.yamanf.taskman.utils.Resource


class WorkspaceViewModel(private val firebaseRepository: FirebaseRepository) : ViewModel() {

    private var _workspaceDetailsLiveData = MutableLiveData<WorkspaceModel?>()
    val workspaceDetailsLiveData: MutableLiveData<WorkspaceModel?>
        get() = _workspaceDetailsLiveData

    private var _unDoneTaskLiveData = MutableLiveData<Resource<ArrayList<TaskModel>>>()
    val unDoneTaskLiveData: MutableLiveData<Resource<ArrayList<TaskModel>>>
        get() = _unDoneTaskLiveData

    private var _doneTaskLiveData = MutableLiveData<ArrayList<TaskModel>>()
    val doneTaskLiveData: MutableLiveData<ArrayList<TaskModel>>
        get() = _doneTaskLiveData

    private var _isDoneRVExpandedLiveData = MutableLiveData<Boolean>()
    val isDoneRVExpandedLiveData: MutableLiveData<Boolean>
        get() = _isDoneRVExpandedLiveData

    init {
        _isDoneRVExpandedLiveData.value = false
    }

    fun changeIsDoneRVExpand(){
       _isDoneRVExpandedLiveData.value = !_isDoneRVExpandedLiveData.value!!
    }

    fun getUnDoneTasksFromWorkspace(workspaceId:String) {
        _unDoneTaskLiveData.value = Resource.Loading()
        firebaseRepository.getAllTasks()
            .whereEqualTo(Constants.WORKSPACE_ID,workspaceId)
            .whereEqualTo(Constants.IS_DONE,false)
            .addSnapshotListener {snapshot, e ->
                if (e != null){
                    return@addSnapshotListener
                }
                if (snapshot != null){
                    val unDoneTaskList = ArrayList<TaskModel>()
                    val documents = snapshot.documents
                    documents.forEach{
                        val undoneTask =  it.toObject(TaskModel::class.java)
                        unDoneTaskList.add(undoneTask!!)
                    }
                    _unDoneTaskLiveData.value = Resource.Success(unDoneTaskList)
                }
            }
    }

    fun getDoneTasksFromWorkspace(workspaceId:String) {
        firebaseRepository.getAllTasks()
            .whereEqualTo(Constants.WORKSPACE_ID,workspaceId)
            .whereEqualTo(Constants.IS_DONE,true)
            .addSnapshotListener {snapshot, e ->
                if (e != null){
                    return@addSnapshotListener
                }
                if (snapshot != null){
                    val doneTaskList = ArrayList<TaskModel>()
                    val documents = snapshot.documents
                    documents.forEach{
                        val doneTask =  it.toObject(TaskModel::class.java)
                        doneTaskList.add(doneTask!!)
                    }
                    _doneTaskLiveData.value = doneTaskList
                }
            }
    }

    fun getWorkspaceDetails(workspaceId: String){
        firebaseRepository.getAllWorkspaces()
            .document(workspaceId)
            .addSnapshotListener{ snapshot, e ->
                if (e != null){
                    return@addSnapshotListener
                }
                if (snapshot != null){
                    val workspaceModel = snapshot.toObject<WorkspaceModel>()
                    _workspaceDetailsLiveData.value = workspaceModel
                }
            }
    }

    fun updateWorkspace(workspaceModel: WorkspaceModel, result:(Boolean)->Unit){
        firebaseRepository.updateWorkspace(workspaceModel){
            return@updateWorkspace result(it)
        }
    }

    fun deleteWorkspace(workspaceId: String,result:(Boolean)->Unit){
        firebaseRepository.deleteWorkspace(workspaceId){
            return@deleteWorkspace result(it)
        }
    }

}
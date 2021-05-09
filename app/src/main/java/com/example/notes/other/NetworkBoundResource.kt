package com.example.notes.other

import kotlinx.coroutines.flow.*

inline fun <ResultType,RequestType> networkBoundResource(
    crossinline query:()->Flow<ResultType>,
    crossinline fetch: suspend ()->RequestType,
    crossinline saveFetchResult: suspend (RequestType)->Unit,
    crossinline onFetchFailed: (Throwable)->Unit = { Unit } ,
    crossinline shouldFetch: (ResultType) -> Boolean = {true}
) = flow {
    emit(Resource.loading(null))

    val data = query().first()
    val flow = if(shouldFetch(data)){
        emit(Resource.loading(data))
        try {
            val fetchResult = fetch()
            saveFetchResult(fetchResult)
            query().map {
                Resource.success(it)
            }
        }catch (e:Throwable){
            onFetchFailed(e)
            query().map { Resource.error("Could not reach the server",it)}
        }
    }else{
        query().map { Resource.success(it) }
    }
    emitAll(flow)
}
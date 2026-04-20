package com.vantechinformatics.easycargo.utils

import androidx.compose.runtime.Composable
import com.vantechinformatics.easycargo.data.ParcelType
import easycargo.composeapp.generated.resources.Res
import easycargo.composeapp.generated.resources.parcel_type_bag
import easycargo.composeapp.generated.resources.parcel_type_box
import easycargo.composeapp.generated.resources.parcel_type_documents
import easycargo.composeapp.generated.resources.parcel_type_envelope
import easycargo.composeapp.generated.resources.parcel_type_furniture
import easycargo.composeapp.generated.resources.parcel_type_other
import easycargo.composeapp.generated.resources.parcel_type_suitcase
import org.jetbrains.compose.resources.stringResource

@Composable
fun parcelTypeLabel(type: ParcelType): String = when (type) {
    ParcelType.BOX -> stringResource(Res.string.parcel_type_box)
    ParcelType.SUITCASE -> stringResource(Res.string.parcel_type_suitcase)
    ParcelType.BAG -> stringResource(Res.string.parcel_type_bag)
    ParcelType.FURNITURE -> stringResource(Res.string.parcel_type_furniture)
    ParcelType.ENVELOPE -> stringResource(Res.string.parcel_type_envelope)
    ParcelType.DOCUMENTS -> stringResource(Res.string.parcel_type_documents)
    ParcelType.OTHER -> stringResource(Res.string.parcel_type_other)
}

@Composable
fun parcelTypeLabel(token: String): String =
    parcelTypeLabel(ParcelType.fromToken(token))

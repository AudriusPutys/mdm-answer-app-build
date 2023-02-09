/*
 * Copyright 2020-2023 University of Oxford and Health and Social Care Information Centre, also known as NHS Digital
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package uk.ac.ox.softeng.maurodatamapper.datamodel.gorm.constraint.validator

import uk.ac.ox.softeng.maurodatamapper.datamodel.item.DataElement
import uk.ac.ox.softeng.maurodatamapper.gorm.constraint.validator.UniqueStringValidator

/**
 * @since 19/04/2018
 */
class DataElementLabelValidator extends UniqueStringValidator<DataElement> {

    DataElementLabelValidator(DataElement object) {
        super(object)
    }

    @Override
    boolean objectParentIsNotSaved() {
        !object.dataClass?.ident()
    }

    @Override
    boolean valueIsNotUnique(String value) {
        object.dataClass.countDataElementsByLabel(value) > 1
    }
}
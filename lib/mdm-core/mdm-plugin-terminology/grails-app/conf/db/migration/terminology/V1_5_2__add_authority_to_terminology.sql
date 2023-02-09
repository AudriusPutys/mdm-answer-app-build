--
-- Copyright 2020 University of Oxford
--
-- Licensed under the Apache License, Version 2.0 (the "License");
-- you may not use this file except in compliance with the License.
-- You may obtain a copy of the License at
--
--     http://www.apache.org/licenses/LICENSE-2.0
--
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed on an "AS IS" BASIS,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the License for the specific language governing permissions and
-- limitations under the License.
--
-- SPDX-License-Identifier: Apache-2.0
--

ALTER TABLE terminology.terminology
    ADD COLUMN authority_id UUID;

ALTER TABLE terminology.terminology
    ADD CONSTRAINT fk7dlm65qgt6m8ptacxycqyhl4m
        FOREIGN KEY (authority_id) REFERENCES core.authority;



ALTER TABLE terminology.code_set
    ADD COLUMN authority_id UUID;

ALTER TABLE terminology.code_set
    ADD CONSTRAINT fk2jwton4ry4smlk76tax1n1j5p
        FOREIGN KEY (authority_id) REFERENCES core.authority;

